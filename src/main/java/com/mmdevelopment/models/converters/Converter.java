package com.mmdevelopment.models.converters;

public interface Converter<M,D> {
    D entityToDto(M entity);
    M dtoToEntity(D dto);
}
