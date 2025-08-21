package com.delivery_tracker_app.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"app.jwt.secret=test-secret-key-that-is-long-enough",
		"app.user.username=testuser",
		"app.user.password=testpass",
		"app.user.roles=ADMIN"
})
class AppApplicationTests {

	@Test
	void contextLoads() {
	}

}
