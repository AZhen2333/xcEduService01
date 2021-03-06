package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.mapstruct.Mapper;
import org.springframework.data.mongodb.repository.MongoRepository;

@Mapper
public interface FileSystemRepository extends MongoRepository<FileSystem, String> {
}
