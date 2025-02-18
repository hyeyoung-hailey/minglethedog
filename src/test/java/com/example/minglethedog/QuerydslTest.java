package com.example.minglethedog;

import com.example.minglethedog.entity.Hello;
import com.example.minglethedog.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuerydslTest {

    @Autowired
    EntityManager entityManager;

    @Test
    void test() {

        Hello hello = new Hello();
        entityManager.persist(hello);

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QHello qHello = new QHello("hello");

        Hello hello1 = queryFactory.select(qHello).from(qHello).fetchFirst();

        assertThat(hello1).isEqualTo(hello);
    }
}
