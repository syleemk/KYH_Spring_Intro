package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

//스프링 이용한 테스트할 때
@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

    // 필드 주입보다는 생성자 주입 쓰는게 좋다했지만
    // 사실 테스트는 제일 끝단에 있는 것이니까 그냥 제일 편한방법 쓰면됨
    // 테스트를 다른데서 갖다 쓸게 아니라 그냥 내가 잘 돌아가나 확인하는 용도니까 ㅋㅋ
    // 필요한거 인젝션해서 쓰고 끝이니까
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository; // 구현체는 SpringConfiguration한데서 올라올 것임 

    
    // afterEach 필요했던 이유
    // 메모리 DB의 데이터를 다음 테스트에 영향을 끼치지 않기 위해 지워주기 위해서
    // Transactional 어노테이션 때문에 필요없어짐

    @Test
    void 회원가입() {
        //given (이런 상황이 주어져서)
        Member member = new Member();
        member.setName("spring1");

        //when (이거를 실행했을때)
        Long saveId = memberService.join(member);

        //then (결과가 이렇게 나와야 해)
        Member result = memberService.findOne(saveId).get();
        assertThat(result.getName()).isEqualTo(member.getName());
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberService.join(member1);
        // 오른쪽 로직을 실행할 건데, 왼쪽 예외가 터져야함 (좋은점 에러 반환함)
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }

}