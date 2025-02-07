package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.Department;
import com.github.aivle6th.ai23.springboot_backend.entity.RoleType;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException{
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with employeeId: "
                     + employeeId));
        
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        for (RoleType role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.toString()));
        }

        if(user.getDepartment() != null){
            Department department = user.getDepartment();

            for(Board board : department.getBoards()){
                authorities.add(new SimpleGrantedAuthority("BOARD_" + board.getBoardId()));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmployeeId(),
                user.getPwd(),
                authorities
            );
    }
}