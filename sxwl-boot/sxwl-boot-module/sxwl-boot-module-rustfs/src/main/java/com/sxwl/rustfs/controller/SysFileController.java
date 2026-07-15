package com.sxwl.rustfs.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.rustfs.model.dto.*;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import com.sxwl.rustfs.service.SysFileService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统文件 Controller
 *
 * <p>提供文件上传（简单/分片）、下载、删除、查询等接口。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rustfs/file")
public class SysFileController {

    private final SysFileService sysFileService;

    public SysFileController(SysFileService sysFileService) {
        this.sysFileService = sysFileService;
    }

    /**
     * 简单上传（小文件不分片）
     *
     * @param file 文件
     * @return 文件信息
     */
    @PostMapping("/simple")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:upload')")
    public SysFileDTO simpleUpload(@RequestParam("file") MultipartFile file) {
        return sysFileService.simpleUpload(file);
    }

    /**
     * 初始化分片上传
     *
     * @param dto 初始化参数
     * @return 上传会话 ID
     */
    @PostMapping("/upload/init")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:upload')")
    public Long initUpload(@RequestBody @Valid UploadInitDTO dto) {
        return sysFileService.initUpload(dto);
    }

    /**
     * 上传分片
     *
     * @param uploadId   会话 ID
     * @param chunkIndex 分片序号
     * @param chunkMd5   分片 MD5
     * @param file       分片二进制
     * @return 上传结果
     */
    @PostMapping("/upload/chunk")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:upload')")
    public UploadChunkDTO uploadChunk(@RequestParam("uploadId") Long uploadId,
                                      @RequestParam("chunkIndex") Integer chunkIndex,
                                      @RequestParam(value = "chunkMd5", required = false) String chunkMd5,
                                      @RequestParam("file") MultipartFile file) {
        return sysFileService.uploadChunk(uploadId, chunkIndex, chunkMd5, file);
    }

    /**
     * 查询已上传分片（断点续传）
     *
     * @param md5 文件 MD5
     * @return 续传信息
     */
    @GetMapping("/upload/{md5}/chunks")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:upload')")
    public ChunkCheckDTO getUploadedChunks(@PathVariable("md5") String md5) {
        return sysFileService.getUploadedChunks(md5);
    }

    /**
     * 合并分片完成上传
     *
     * @param dto 合并请求
     * @return 文件信息
     */
    @PostMapping("/upload/complete")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:upload')")
    public SysFileDTO completeUpload(@RequestBody @Valid UploadCompleteDTO dto) {
        return sysFileService.completeUpload(dto);
    }

    /**
     * 秒传检查
     *
     * @param md5 文件 MD5
     * @return 已存在的文件信息，不存在返回 null
     */
    @GetMapping("/check-md5")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:upload')")
    public SysFileDTO checkMd5(@RequestParam("md5") String md5) {
        return sysFileService.checkMd5(md5);
    }

    /**
     * 下载文件
     *
     * @param id 文件 ID
     * @return 文件流
     */
    @GetMapping("/download/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:download')")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) {
        return sysFileService.downloadFile(id);
    }

    /**
     * 获取预签名 URL
     *
     * @param id 文件 ID
     * @return 预签名 URL
     */
    @GetMapping("/presigned-url/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:download')")
    public String getPresignedUrl(@PathVariable("id") Long id) {
        return sysFileService.getPresignedUrl(id);
    }

    /**
     * 分页查询文件列表
     *
     * @param params 分页查询参数
     * @return 分页文件列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:list')")
    public PageInfo<SysFileDTO> getFilePageByParams(@Valid SysFilePageParams params) {
        return sysFileService.getFilePageByParams(params);
    }

    /**
     * 删除文件
     *
     * @param id 文件 ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:file:delete')")
    public void deleteFile(@PathVariable("id") Long id) {
        sysFileService.deleteFile(id);
    }
}
