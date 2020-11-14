package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// 리포지토리는 그냥 넣었다 뺐다 하는 느낌인데
// 서비스 클래스는 메서드 네이밍이 좀 더 비즈니스에 가까움
// 서비스는 비즈니스에 가까운 네이밍 써야함
//@Service // 스프링 컨테이너에 멤버서비스 등록
@Transactional
/**
 * Jpa 쓰려면 항상 transactional있어야함
 * 데이터를 저장하거나 변경할 때는 transactional 있어야함
 * Jpa는 모든 데이터 변경이 트랜잭션 안에서 실행되어야함
 */
public class MemberService {

    private final MemberRepository memberRepository;// = new MemoryMemberRepository();

    //memberRepository를 직접 생성하는 게 아니라, 외부에서 넣어주도록 바꿈
    //서비스 어노테이션은 스프링 컨테이너 뜰 때, 스프링이 생성하도록 해줌
    // 생성될 때 생성자 호출
    // 생성자에 autowired있으면 의존성 주입함(컨테이너의 객체를 넣어줌)
    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    /**
     * 회원가입
     */
    public Long join(Member member){

/*      // Optional 팁 (Optional바로 반환 받는거 좋지않음 아래처럼) (안이쁨 ㅋㅋ)

        //같은 이름이 있는 중복 회원은 안된다.
        Optional<Member> result = memberRepository.findByName(member.getName());
        result.ifPresent(m -> { //값이 있으면 동작, Optional이기 때문에 가능
            //예외 발생시킴 (throw해줌 호출한 곳에)
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
*/
        // 무언가 로직이 쭉 나오는 경우 메서드로 뽑는게 좋음
        validateDuplicateMember(member); // 중복회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // 반환받지 않고 바로 메서드로 연결해서 쓰는게 좋음
        memberRepository.findByName(member.getName())
            .ifPresent(m -> { // 중복회원 예외 터지는지 테스트 해봐야함
                throw new IllegalStateException("이미 존재하는 회원입니다.");
            });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
