package hello.hellospring;

import hello.hellospring.repository.*;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

//스프링 빈이 뜰때 configuration 읽어오고
// bean 표시보고 등록한다
@Configuration
public class SpringConfig {
/*
    //properties설정한 datasource를 bean등록해줌 스프링이
    private DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    // spring bean을 등록한다는 표시
    // 아래 로직을 호출해서 스프링 빈에 등록해줌
*/
/*

    private EntityManager em;

    @Autowired
    public SpringConfig(EntityManager em) {
        this.em = em;
    }
*/

    private final MemberRepository memberRepository;

    @Autowired //생성자 하나인 경우 생략 가능
    // 스프링 컨테이너에서 등록한 멤버 리퍼지토리를 찾음
    // 내가 등록한 것은 없음
    // 내가 한 것은 인터페이스만 만들어놓은 것 뿐
    // 나머지는 스프링 데이터 JPA가 구현체 만들고 등록까지 해줌
    // 그래서 우리는 인젝션을 받을 수 있음
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService() {
//        return new MemberService(memberRepository()); // 의존성 주입
        return new MemberService(memberRepository);
    }

/*
    @Bean
    public MemberRepository memberRepository(){
        //return new MemoryMemberRepository();
        
        //configuration외에 다른 어떤 코드도 손대지 않고 db 바꿈
        //return new JdbcMemberRepository(dataSource);

        //return new JdbcTemplateMemberRepository(dataSource);
        //return new JpaMemberRepository(em);
    }
*/
}
