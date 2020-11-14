package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemberRepository implements MemberRepository{

    //db에 붙으려면 datasource 필요함
    private final DataSource dataSource;

    //spring한테 datasource 주입받아야함 (properties에 datasource 설정해둠)
    //세팅해놓으면 스프링 부트가 datasource를 만들어 놓음, 이걸 주입받으면 됨
    @Autowired
    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;

        //getConnection을 통해 커넥션 얻을 수 있음(db와 연결되는 소켓)
        //코딩 어마어마 함 ㅋ
    }

    @Override
    public Member save(Member member) {
/*      수도코드
        
        //쿼리 짜야함 ㅋ
        String sql = "insert into member(name) values(?)";
        //dataSource로 부터 connection얻어온 후
        Connection connection = dataSource.getConnection();
        //sql 작성함
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, member.getName());
        //db에 쿼리 날림
        preparedStatement.executeUpdate();
        
        + 여기에 멤버의 id 자동으로 설정해주는 작업해줘야하는데, 그러기 위해선 
        db에서 생성된 id를 가져오는 작업도 포함해야함
*/
        //?는 파라미터 매핑위해 쓰는 것
        String sql = "insert into member(name) values(?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        //결과를 받는 것
        ResultSet rs = null;
        
        //얘네들이 Exception 엄청 던져서 try catch잘해줘야함
        //자원들도 release 해줘야함
        //db커넥션같은 경우 네트워크 연결된 것이기 때문에 쓰고나면 리소스 다 끊어야함
        //안그러면 커넥션 계속 쌓이다가 대 장애 남ㅋㅋ
        try {
            //커넥션 가져옴
            conn = getConnection();
            //날릴 sql문 준비
            pstmt = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS); //자동생성되는 키값 반환설정
            //sql문의 첫번째 물음표(파라미터)랑 name이랑 매핑됨
            pstmt.setString(1, member.getName());
            //db에 실제 쿼리 날림
            pstmt.executeUpdate();
            //반환된 키값 받아옴
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            //조회는 update가 아니라 그냥 query다
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {close(conn, pstmt, rs);
        }
    }
    @Override
    public List<Member> findAll() {
        String sql = "select * from member";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                members.add(member);
            }
            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    
    
    //데이터 소스에서 내가 직접 getConnection할 수도 있는데
    //그렇게되면 계속 새로운 커넥션이 주어짐
    //참고만 해둬라 이거는
    //스프링프레임워크를 통해서 데이터 소스를 쓸 때는 dataSourceUtils를 통해서 커넥션 얻어야함
    //그래야 이전에 트랜잭션이 걸릴 수 있는데 그런경우 커넥션을 유지시켜야함
    //이렇게해야 커넥션 유지시켜줌
    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }
    
    //리소스를 역순으로 릴리즈 해줌
    //존나 복잡하구나 정도 알고있으면 됨
    //try with resource문법으로 해도 되는데, 어차피 안쓰니까 고전스타일로 적음 ㅋㅋ
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs)
    {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //커넥션 닫을 때에도 datasourceUtils를 통해서 릴리즈해줘야함
    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

}
