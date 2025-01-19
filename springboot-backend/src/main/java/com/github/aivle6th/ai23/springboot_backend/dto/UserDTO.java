package com.github.aivle6th.ai23.springboot_backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String employeeId;
    private String userName;
    private String email;
    private String pwd;
    private String deptId;


//    public static class Post{
//        private String employeeId;
//        private String userName;
//        private String email;
//        private String pwd;
//        private String deptId;
//    }

}
