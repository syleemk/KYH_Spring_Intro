package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

//스프링 실행될 때 제일 먼저 Spring Container라는 통이 생김
//그 컨테이너에 Controller어노테이션이 있으면
//거기에 MemberController 객체 생성해서 넣어두고 Spring이 관리함
@Controller
public class MemberController {

    //new키워드로 객체 생성하면 안됨
    //스프링 컨테이너에 등록하고 스프링 컨테이너로 부터 받아서 사용해야함
    //new키워드로 생성하면 여러 다른 컨트롤러들이 멤버서비스 사용할 수 있는데 모두 다른객체 생성해서 사용
    //하나만 생성해서 공유해서 사용해야함 -> 스프링 컨테이너에 등록해서 쓰면 됨
    private final MemberService memberService; // = new MemberService();

    // 멤버 컨트롤러를 스프링이 컨테이너가 뜰때 생성을 한다했음
    // 그러면 생성자 호출되는데, 여기 autowired 되어있으면
    // 여기있는 멤버서비스를 스프링이 스프링 컨테이너에 있는 멤버서비스 빈 객체를 가져다 연결시켜줌(DI)
    // 그러기위해서는 멤버서비스도 컨테이너에 등록되어있어야함
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    @PostMapping("members/new")
    public String create(MemberForm form) {
        Member member= new Member();
        member.setName(form.getName());
        memberService.join(member);

        return "redirect:/";
    }

    @GetMapping("members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);
        return "members/memberList";
    }
}
