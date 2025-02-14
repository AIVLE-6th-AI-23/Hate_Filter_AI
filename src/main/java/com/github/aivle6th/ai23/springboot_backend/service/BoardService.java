package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.BoardRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.Department;
import com.github.aivle6th.ai23.springboot_backend.repository.BoardRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.DepartmentRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final DepartmentRepository departmentRepository;

    // 게시판 생성
    public Board createBoard(BoardRequestDto requestDto) {
        Board board = Board.builder()
                .boardTitle(requestDto.getBoardTitle())
                .description(requestDto.getDescription())
                .endDate(requestDto.getEndDate())
                .build();

        if (requestDto.getDeptIds() != null && !requestDto.getDeptIds().isEmpty()) {
            Set<Department> departments = new HashSet<>(departmentRepository.findAllById(requestDto.getDeptIds()));

            List<String> missingIds = requestDto.getDeptIds().stream()
                    .filter(id -> departments.stream().noneMatch(dept -> dept.getDeptId().equals(id)))
                    .collect(Collectors.toList());

            if (!missingIds.isEmpty()) {
                throw new EntityNotFoundException("부서를 찾을 수 없습니다: " + String.join(", ", missingIds));
            }

            board.setDepartments(departments);
        }

        return boardRepository.save(board);
    }

    // 현재 진행 중인 게시판 부서별 조회
    public List<Board> getActiveBoardsByDepartment(String deptId, LocalDateTime cursor, int size) {   
        return boardRepository.findActiveBoardsByDepartmentWithCursor(deptId, cursor, PageRequest.of(0, size));
    }

    // 현재 완료된 게시판 부서별 조회
    public List<Board> getCompletedBoardsByDepartment(String deptId, LocalDateTime cursor, int size) {   
        return boardRepository.findCompletedBoardsByDepartmentWithCursor(deptId, cursor, PageRequest.of(0, size));
    }

    // 부서별 게시판 전체 조회
    public List<Board> getBoardsByDepartment(String deptId, LocalDateTime cursor, int size) {  
        return boardRepository.findBoardsByDepartmentWithCursor(deptId, cursor, PageRequest.of(0, size));
    }


    // 게시판 상세 조회
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + boardId));
    }

    // 전체 게시판 조회
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 게시판 수정
    public Board updateBoard(Long boardId, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + boardId));

        board.updateBoard(
            requestDto.getBoardTitle(),
                requestDto.getDescription(),
                requestDto.getEndDate()
        );

        if (requestDto.getDeptIds() != null && !requestDto.getDeptIds().isEmpty()) {
            Set<Department> departments = new HashSet<>(departmentRepository.findAllById(requestDto.getDeptIds()));

            List<String> missingIds = requestDto.getDeptIds().stream()
                    .filter(id -> departments.stream().noneMatch(dept -> dept.getDeptId().equals(id)))
                    .collect(Collectors.toList());

            if (!missingIds.isEmpty()) {
                throw new EntityNotFoundException("부서를 찾을 수 없습니다: " + String.join(", ", missingIds));
            }

            board.setDepartments(departments);
        }

        return boardRepository.save(board);
    }

    // 게시판 삭제
    public void deleteBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new EntityNotFoundException("Board not found: " + boardId);
        }
        boardRepository.deleteById(boardId);
    }

}
