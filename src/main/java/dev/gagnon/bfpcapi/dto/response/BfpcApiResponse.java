package dev.gagnon.bfpcapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BfpcApiResponse<T> {
    private boolean status;
    private T data;
}
