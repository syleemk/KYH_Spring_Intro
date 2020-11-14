package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 테스트는 과감하게 한글로 적어도 됨
 * 어차피 빌드될 때 포함되는 코드도 아니고
 * 직관적으로 알아보기 쉬운 것이 좋음
 */
// 순수 자바 코드로만 되어있는 테스트
// JVM 위에서만 동작하니까 굉장히 빨리 실행
class MemberServiceTest {

    MemberService memberService;// = new MemberService();
    //애매한게 있음,서비스에서 연결된 리포지토리랑 여기서 new로 생성해준 리포지토리랑 다른객체임
    //여기서 오류가 안나는 것은 hashmap이 static으로 선언되어서 인스턴스간 공유되서 상관없는 것이긴 함
    //그래서 멤버서비스에서 리포지토리 주입받을 때 new키워드보다는 생성자 주입을 해야한다는 것임
    //그래야 같은 인스턴스를 쓰도록 할 수 있음
    MemoryMemberRepository memberRepository;// = new MemoryMemberRepository();
    
    //member서비스 생성할 때마다 메모리 리포지터리를 직접 넣어주어야함
    //각 테스트를 실행하기 전에 아래 코드를 실행해서 리포지토리와 서비스 생성함
    @BeforeEach
    public void beforeEach() {
        // 각각의 테스트 전에 메모리 레포지토리를 만들고
        // 서비스에 넣어줌 (주입)
        // 그렇게 되면 같은 메모리 리포지토리가 사용이 될 것임
        // 이를 멤버 서비스 입장에서 외부에서 리포지토리를 넣음당했으므로
        // Dependency Injection (DI) 라고 함
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    /**
     * given, when, then문법
     */
    @Test
    void 회원가입() {
        //given (이런 상황이 주어져서)
        Member member = new Member();
        member.setName("hello");

        //when (이거를 실행했을때)
        Long saveId = memberService.join(member);

        //then (결과가 이렇게 나와야 해)
        Member result = memberService.findOne(saveId).get();
        assertThat(result.getName()).isEqualTo(member.getName());
    }

    // 테스트는 정상플로우도 중요한데, 예외플로우는 훨씬 중요함
    // 위의 테스트는 반쪽짜리 테스트임
    // join의 테스트의 핵심은 중복회원 예외가 터지는지 보는 것임
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
/*
        try {
            memberService.join(member2); //여기서 예외가 발생해야 테스트 통과
            fail();
        } catch (IllegalStateException e){
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
*/

        //then
    }
    

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}