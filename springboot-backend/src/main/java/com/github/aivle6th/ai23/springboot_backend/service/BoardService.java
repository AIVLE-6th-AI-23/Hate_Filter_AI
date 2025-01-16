package com.github.aivle6th.ai23.springboot_backend.service;

import com.github.aivle6th.ai23.springboot_backend.dto.BoardCreateRequestDto;
import com.github.aivle6th.ai23.springboot_backend.entity.Board;
import com.github.aivle6th.ai23.springboot_backend.entity.BoardDepartment;
import com.github.aivle6th.ai23.springboot_backend.entity.Department;
import com.github.aivle6th.ai23.springboot_backend.repository.BoardDepartmentRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.BoardRepository;
import com.github.aivle6th.ai23.springboot_backend.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final DepartmentRepository departmentRepository;
    private final BoardDepartmentRepository boardDepartmentRepository;

    // 부서별 게시판 조회
    public List<Board> getBoardsByDepartment(String deptId) {
        return boardRepository.findByBoardDepartments_Department_DeptId(deptId);
    }

    // 게시판 상세 조회
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + boardId));
    }

    // 게시판 삭제
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + boardId));

        // 연관된 BoardDepartment 먼저 삭제
        boardDepartmentRepository.deleteByBoard(board);

        // Board 삭제
        boardRepository.delete(board);
    }

    // 전체 게시판 조회
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 게시판 생성
    public Board createBoard(BoardCreateRequestDto request) {
        // 1. Board 엔티티 생성
        Board board = Board.builder()
                .boardTitle(request.getBoardTitle())
                .description(request.getDescription())
                .isPublic(request.getIsPublic())
                .endDate(request.getEndDate())
                .build();

        // 2. Board 저장
        Board savedBoard = boardRepository.save(board);

        // 3. BoardDepartment 관계 설정 및 저장
        if (request.getDeptIds() != null && !request.getDeptIds().isEmpty()) {
            for (String deptId : request.getDeptIds()) {
                Department department = departmentRepository.findById(deptId)
                        .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다: " + deptId));

                BoardDepartment boardDepartment = BoardDepartment.builder()
                        .board(savedBoard)
                        .department(department)
                        .build();

                boardDepartmentRepository.save(boardDepartment);
            }
        }

        return savedBoard;
    }

    // 게시판 수정
    public Board updateBoard(Long boardId, BoardCreateRequestDto request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다: " + boardId));

        // 1. 게시판 정보 업데이트
        board.updateBoard(
                request.getBoardTitle(),
                request.getDescription(),
                request.getIsPublic(),
                request.getEndDate()
        );

        // 2. 기존 부서 연관관계 삭제
        boardDepartmentRepository.deleteByBoard(board);

        // 3. 새로운 부서 연관관계 설정
        if (request.getDeptIds() != null && !request.getDeptIds().isEmpty()) {
            for (String deptId : request.getDeptIds()) {
                Department department = departmentRepository.findById(deptId)
                        .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다: " + deptId));

                BoardDepartment boardDepartment = BoardDepartment.builder()
                        .board(board)
                        .department(department)
                        .build();

                boardDepartmentRepository.save(boardDepartment);
            }
        }

        return boardRepository.save(board);
    }

}
