package com.nexsol.tpa;

import com.nexsol.tpa.core.domain.admin.MemoRegistrar;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("local")
@Tag("context")
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class ContextTest {

	@MockitoBean
	protected MemoRegistrar memoRegistrar;

}
