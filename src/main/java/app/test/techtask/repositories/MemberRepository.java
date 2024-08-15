package app.test.techtask.repositories;

import app.test.techtask.data.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}