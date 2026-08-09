package com.example.accountbook.book.dto;

import jakarta.validation.constraints.NotBlank;

public class BookRequest {
    @NotBlank(message = "账本名称不能为空")
    private String name;
    private String description;
    private String currency = "CNY";
    private String cover;
    private Boolean isDefault = false;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
