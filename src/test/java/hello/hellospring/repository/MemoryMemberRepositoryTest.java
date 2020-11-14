package hello.hellospring.repository;

import hello.hellospring.domain.Member;
//import org.junit.jupiter.api.Assertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


// 우리는 먼저 리포지토리를 개발 후 테스트 코드 작성했는데,
// 테스트 코드 먼저 작성후, 개발 진행할 수도 있음
// 검증할 수 있는 틀을 먼저 만들어놓고, 제품(구현클래스)를 만드는 것
// 그것이 바로 TDD(Test Driven Development)라고 함
public class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    //각각의 메서드 실행 끝날 때 마다 실행되는 메서드(콜백이라 보면 됨)
    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("sooyoung");

        repository.save(member);

        //반환 타입 optional에서 값 꺼낼 때는 get메서드 사용
        Member result = repository.findById(member.getId()).get();

        // 이렇게 문자열로 출력해봐도 됨
        System.out.println("result = " + (result == member));

        // junit core 클래스
        // expected, actual
        //Assertions.assertEquals(member, null);

        //assertj에서 제공하는 메서드, 이게 더 편함
        assertThat(result).isEqualTo(member);
    }


    // 여기서 처음에는 에러 안나는데 두번째에서 에러남
    // 메서드 테스트 순서는 보장되지 않음
    // 따라서 모든 테스트는 순서 상관없이 메서드별로 따로 동작하도록 만들어야함
    // findAll이 먼저 테스트 되면서 spring1 spring2가 먼저 저장되버려서 다른 객체가 리턴된 것
    // 따라서 테스트 끝나고 나면 리포지토리 clear해줘야함
    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        // shift + f6하면 여러개 이름 한번에 바뀜
        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        Member result = repository.findByName("spring1").get();

        assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        List<Member> result = repository.findAll();
        assertThat(result.size()).isEqualTo(2);
    }
}
