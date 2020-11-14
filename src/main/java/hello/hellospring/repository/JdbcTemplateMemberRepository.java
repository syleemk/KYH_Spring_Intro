package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//환경설정은 순수 Jdbc랑 같음(의존성 설정 등)
public class JdbcTemplateMemberRepository implements MemberRepository{
    
    //jdbc template이 있어야함 (injection받을 수 있는 것은 아님)
    //template이라는 이름 쓰는 이유는, 디자인 패턴중에 template패턴 써서 메서드가 그러한 패턴 보임
    private final JdbcTemplate jdbcTemplate;

    /**
     * 생성자가 딱 1개만 있을 때, Spring Bean으로 등록된다면 (Spring Config에서 등록)
     * autowired 생략가능
     * 생성자가 여러 개 있으면 안됨!
     */
    @Autowired // dataSource가 필요함, 인젝션 받음
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //도큐먼트 보고 하면 할 수 있음
    @Override
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("member").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", member.getName());
        Number key = jdbcInsert.executeAndReturnKey(new
                MapSqlParameterSource(parameters));
        member.setId(key.longValue());
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        //쿼리 날린 결과를 row mapper라는 것으로 매핑해줘야함
        List<Member> result = jdbcTemplate.query("select * from member where id=?", memberRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = jdbcTemplate.query("select * from member where name = ?", memberRowMapper(), name);
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("select * from member", memberRowMapper());
    }

    private RowMapper<Member> memberRowMapper() {
        //resultSet이 여기로 넘어옴
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("id"));
            member.setName(rs.getString("name"));
            return member;
        };
    }
}
