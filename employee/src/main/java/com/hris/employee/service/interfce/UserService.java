package com.hris.employee.service.interfce;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    <T> Page<T> searchUsersByUsername(String username, Pageable pageable, Class<T> type);

}
