package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//JpaRepository를 상속하면 
//스프링 데이터 JPA가 자동으로 구현체를 만들어주고, 얘를 스프링 빈에 등록해줌
//우리는 그냥 그걸 가져다 쓰면 됨
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
    
    // 이거 적어주면 끝남 ㅋㅋㅋㅋㅋㅋㅋㅋ
    // .....? 구현해야죠
    // 여러분... 구현할게 없습니다 ㅋㅋㅋㅋㅋ

    /**
     * 명명 규칙 맞춰서 메서드 작성해주면
     * JPA가 알아서 쿼리 생성해줌(jpql)
     * select m from Member m where m.name = ?
     */
    @Override
    Optional<Member> findByName(String name);
}
