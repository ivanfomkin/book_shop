package com.example.MyBookShopApp;

import com.example.MyBookShopApp.config.TestContainersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TestContainersConfig.class)
@AutoConfigureMockMvc
public abstract class AbstractTest {

    @Autowired
    protected MockMvc mockMvc;
}
