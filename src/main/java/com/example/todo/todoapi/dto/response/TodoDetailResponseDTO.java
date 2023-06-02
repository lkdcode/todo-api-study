package com.example.todo.todoapi.dto.response;

import com.example.todo.todoapi.entity.Todo;
import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDetailResponseDTO {
    private String id;
    private String title;
    private boolean done;

    // 엔티티를 DTO 로 만들어 주는 생성자
    public TodoDetailResponseDTO(Todo todo) {
        this.id = todo.getTodoId();
        this.title = todo.getTitle();
        this.done = todo.isDone();
    }

}
