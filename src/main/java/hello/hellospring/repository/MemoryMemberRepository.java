package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

//@Repository // 스프링 컨테이너에 등록
public class MemoryMemberRepository implements MemberRepository{
    //저장할 저장소
    private static Map<Long, Member> store = new HashMap<>();
    //key값 생성해주는 얘
    private static Long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member); //메모리 db에 저장
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        //과거에는 그냥 반환했지만, 요즘에는 null값이 반환될 가능 있으면 optional로 감싸줌
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        //Java8의 람다와 스트림 그리고 Collection 문법
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    //자바 실무에서는 list많이 씀 (루프돌리기도 편하고 해서)
    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
