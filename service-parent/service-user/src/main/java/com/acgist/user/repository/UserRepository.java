package com.acgist.user.repository;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.acgist.data.query.TemplateQuery;
import com.acgist.user.pojo.dto.UserDto;
import com.acgist.user.pojo.entity.UserEntity;
import com.acgist.user.pojo.query.UserQuery;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findByName(String name);
    
    @TemplateQuery(
        query = "insert into t_user(id, name, password, create_date, modify_date) values (:id, :name, :password, now(), now())",
        nativeQuery = true
    )
    default void insert(UserEntity userEntity) {
    }
    
    @TemplateQuery(
        query = "update t_user name = :name, modify_date = now() where id = :id",
        nativeQuery = true
    )
    default void update(UserEntity userEntity) {
    }
    
    @TemplateQuery(
        query = "delete from t_user where id = :id",
        nativeQuery = true
    )
    default void delete(Long id) {
    }
    
    @TemplateQuery(
        query = "select name, memo from t_user where id is null",
        fallback = true
    )
    default UserDto fallback() {
        final UserDto userDto = new UserDto();
        userDto.setName("fallback");
        userDto.setMemo("fallback");
        return userDto;
    }
    
    @TemplateQuery(
        query = "select name, memo, (select count(*) from t_user) size from t_user",
        where = "$(name != null) and name = :name",
        sorted = "order by id desc",
        attach = "limit 1",
        nativeQuery = true
    )
    default UserDto query(String name) {
        return null;
    }
    
    @TemplateQuery(
        query = "select name, memo, (select count(*) from t_user) size from t_user",
        where = "$(name != null) and name = :name\n"
            + "$(beginDate != null) and create_date > :beginDate"
            + "$(endDate != null) and create_date < :endDate",
        sorted = "order by id desc",
        attach = "limit 1",
        nativeQuery = true
    )
    default UserDto query(UserQuery userQuery) {
        return null;
    }
    
    @TemplateQuery(
        query = "select name, memo, (select count(*) from t_user) size from t_user",
        where = "$(name != null) and name = :name\n"
            + "$(beginDate != null) and create_date > :beginDate"
            + "$(endDate != null) and create_date < :endDate",
        sorted = "order by id desc",
        attach = "limit 1",
        nativeQuery = true
    )
    default UserDto query(Map<String, Object> queryMap) {
        return null;
    }
    
    @TemplateQuery(
        query = "select name, memo, (select count(*) from t_user) size from t_user",
        where = "$(name != null) and name = :name\n"
            + "$(beginDate != null) and create_date > :beginDate"
            + "$(endDate != null) and create_date < :endDate",
        sorted = "order by id desc",
        attach = "limit 1",
        nativeQuery = true
    )
    default UserDto query(String name, Date beginDate, Date endDate) {
        return null;
    }
    
    @TemplateQuery(
    	query = "select name, memo from t_user",
    	clazz = UserDto.class,
    	nativeQuery = true
	)
    default List<UserDto> queryList() {
    	return null;
    }
    
    @TemplateQuery(
        query = "select name, memo from t_user",
        clazz = UserDto.class,
        nativeQuery = true
    )
    default Page<UserDto> queryList(Pageable pageable) {
        return null;
    }
    
    @TemplateQuery(
        query = "select name, memo from t_user",
        where = "$(name != null) and name = :name\n"
            + "$(beginDate != null) and create_date > :beginDate"
            + "$(endDate != null) and create_date < :endDate",
        clazz = UserDto.class,
        nativeQuery = true
    )
    default Page<UserDto> queryList(UserQuery userQuery, Pageable pageable) {
        return null;
    }
	
}
