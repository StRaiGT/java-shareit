package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.common.markers.Create;
import ru.practicum.shareit.common.markers.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@ToString
public class UserDto {
    Long id;
    String name;

    @NotBlank(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    String email;
}
