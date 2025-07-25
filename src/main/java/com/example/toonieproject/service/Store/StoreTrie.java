package com.example.toonieproject.service.Store;


import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.repository.Store.StoreRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class StoreTrie {

    private final StoreRepository storeRepository;
    private final Node rootNode = new Node();

    // 서버 실행 시 자동으로 Trie 초기화
    @PostConstruct
    public void initTrie() {
        List<Store> stores = storeRepository.findAll();
        for (Store store : stores) {
            insertStore(store);
        }
    }

    // Trie 가게 추가 (초성 포함)
    @Transactional
    public void insertStore(Store store) {

        String name = store.getName().replaceAll("\\s+", "");;

        // 모든 접미사를 Trie에 삽입
        for (int i = 0; i < name.length(); i++) {
            insertToTrie(name.substring(i), store);
        }
    }

    private void insertToTrie(String key, Store store) {
        Node node = rootNode;
        for (char ch : key.toCharArray()) { // 가게 이름을 한 글자씩 분리해서 탐색
            // Trie 계층 구조로 저장
            // ch가 포함된 자식 노드가 없으면 새로운 node를 생성하고, 있으면 해당 노드로 이동
            node = node.childNodes.computeIfAbsent(ch, k -> new Node());
        }

        node.stores.add(store); // 마지막 노드에 가게 정보를 저장
        node.isContainWord  = true; // 해당 노드가 하나의 완성된 단어임을 표시
    }

    // 해당 가게와 관련된 노드를 모두 제거
    @Transactional
    public void deleteStore(Store store) {
        String name = store.getName().replaceAll("\\s+", "");

        for (int i = 0; i < name.length(); i++) {
            deleteFromTrie(name.substring(i), store);
        }
    }

    private void deleteFromTrie(String key, Store store) {
        deleteRecursive(rootNode, key, 0, store);
    }

    // 재귀적으로 노드를 따라가며 해당 store 제거
    private boolean deleteRecursive(Node current, String key, int index, Store targetStore) {
        if (index == key.length()) {
            // storeId가 같은 항목만 제거
            current.stores.removeIf(store -> store.getId().equals(targetStore.getId()));
            if (current.stores.isEmpty()) {
                current.isContainWord = false;
            }
            return current.childNodes.isEmpty() && current.stores.isEmpty();
        }

        char ch = key.charAt(index);
        Node next = current.childNodes.get(ch);
        if (next == null) return false;

        boolean shouldDeleteCurrentNode = deleteRecursive(next, key, index + 1, targetStore);

        if (shouldDeleteCurrentNode) {
            current.childNodes.remove(ch);
            return current.childNodes.isEmpty() && current.stores.isEmpty();
        }

        return false;
    }

    @Transactional
    public void updateStore(Store store) {
        deleteStore(store);  // 기존 이름으로 등록된 정보 제거
        insertStore(store);  // 새 정보 삽입
    }


    public List<Store> searchStore(String query) {

        Set<Store> result = new HashSet<>();
        String lowerQuery = query.toLowerCase(); // 대소문자 구분 없이 비교

        // 쿼리를 포함하는 노드 검색
        Node node = searchSearchWord(lowerQuery);
        if (node == null) return new ArrayList<>(); // 해당하는 단어가 없으면 빈 리스트 반환


        // Trie를 BFS방식으로 탐색하여 결과에 추가
        Queue<Node> queue =  new LinkedList<>();
        queue.offer(node);

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            // 해당 노드가 완전한 단인 경우 해당 노드의 가게들 추가
            if (currNode.isContainWord) {
                result.addAll(currNode.stores);
            }

            // 자식 노드를 큐에 추가하여 계속 탐색
            queue.addAll(currNode.childNodes.values());
        }

        // 정확도 계산
        List<Store> sortedResult = new ArrayList<>(result); // Set에서 List로 변환
        sortedResult.sort((store1, store2) -> {
            int score1 = getMatchingScore(lowerQuery, store1.getName());
            int score2 = getMatchingScore(lowerQuery, store2.getName());

            return (score2 - score1); // 높은 점수 순으로 정렬
        });

        // 상위 5개만 반환
        // return sortedResult.size() > 5 ? sortedResult.subList(0, 5) : sortedResult;

        // 리스트 전체 반환
        return sortedResult;
    }

    // 쿼리와 가게 이름의 정확도 계산
    private int getMatchingScore(String query, String storeName) {

        int score = 0;
        String lowerQuery = query.toLowerCase();
        String lowerStoreName = storeName.toLowerCase();

        // 검색어와 가게 이름을 단어 단위로 분리
        String[] queryWords = lowerQuery.split("\\s+");  // 공백 기준으로 단어 분리
        String[] storeWords = lowerStoreName.split("\\s+");

        // 각 검색어 단어가 가게 이름에 얼마나 포함되는지 계산
        for (String qWord : queryWords) {
            for (String sWord : storeWords) {
                // 단어가 포함되면 단어 길이 만큼 점수 증가
                if (sWord.contains(qWord)) score += qWord.length();
            }
        }

        return score;
    }

    // 쿼리와 일치하는 노드를 찾는 메소드
    private Node searchSearchWord(String query) {
        Node node = rootNode;

        for (char ch : query.toCharArray()) {
            node = node.childNodes.getOrDefault(ch, null);
            if (node == null) return null;
        }

        return node;
    }

}


@ToString
@Setter
@Getter
class Node {
    Map<Character, Node> childNodes = new HashMap<>();

    String word;
    List<Store> stores = new ArrayList<>(); // 가게 리스트

    boolean isContainWord; // 이 노드가 완전한 단어인지 확인하는 플래그
    //boolean isEndOfWord;
}