package com.lilac;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class LilacForumApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testUuid() {
		System.out.println(UUID.randomUUID());
	}
}
