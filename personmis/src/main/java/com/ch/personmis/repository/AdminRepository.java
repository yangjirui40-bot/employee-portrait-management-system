package com.ch.personmis.repository;
import com.ch.personmis.entity.UserEntity;
public interface AdminRepository {
    UserEntity login(UserEntity buser);
}
