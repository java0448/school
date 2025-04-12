package ru.t1.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.t1.school.dto.TaskDTO;
import ru.t1.school.entity.Task;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDTO toDTO(Task task);

    Task toEntity(TaskDTO taskDTO);
}
