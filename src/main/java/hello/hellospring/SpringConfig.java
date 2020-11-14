package hello.hellospring;

import hello.hellospring.repository.JdbcMemberRepository;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//스프링 빈이 뜰때 configuration 읽어오고
// bean 표시보고 등록한다
@Configuration
public class SpringConfig {
    //properties설정한 datasource를 bean등록해줌 스프링이
    private DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    // spring bean을 등록한다는 표시
    // 아래 로직을 호출해서 스프링 빈에 등록해줌
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository()); // 의존성 주입
    }

    @Bean
    public MemberRepository memberRepository(){
        //return new MemoryMemberRepository();
        
        //configuration외에 다른 어떤 코드도 손대지 않고 db 바꿈
        return new JdbcMemberRepository(dataSource);
    }
}
