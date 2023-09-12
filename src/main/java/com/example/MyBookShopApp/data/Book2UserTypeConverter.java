package com.example.MyBookShopApp.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class Book2UserTypeConverter implements AttributeConverter<Book2UserTypeDto, String> {
    @Override
    public String convertToDatabaseColumn(Book2UserTypeDto book2UserTypeDto) {
        if (book2UserTypeDto == null) {
            return null;
        }
        return book2UserTypeDto.getValue();
    }

    @Override
    public Book2UserTypeDto convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Book2UserTypeDto.values())
                .filter(c -> c.getValue().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
