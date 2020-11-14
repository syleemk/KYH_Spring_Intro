package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository{

    //jpa는 entity manager라는 것으로 동작을 함
    //build.gradle에 data jpa 추가해주면
    //스프링부트가 알아서 현재 db와 연결까지 해서 em 생성해줌
    //그래서 그걸 인젝션받으면 됨
    //얘는 내부적으로 이전시간에 했던 datasource 등을 다 들고있어서
    //내부적으로 다 처리함
    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        //persist : 영속하다 영구저장하다
        //이렇게만 하면 끝남, jpa가 다 알아서 해줌
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        //이제까진 테이블 대상으로 쿼리 날렸음
        //jpql이라는 객체 대상 쿼리임 (정확하겐 member 엔티티 대상)
        //그럼 이게 sql로 번역이 됨
        //select에서 멤버 엔티티 자체를 select함(조회함)
        return em.createQuery("select m form Member m", Member.class)
                .getResultList();
    }
}
