package com.github.talkwithkeyboard;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "oss-upload")
public class OssUploadMojo extends AbstractMojo {

    @Parameter(property = "oss-upload.endpoint", defaultValue = "http://oss-cn-hangzhou.aliyuncs.com")
    private String endpoint;

    @Parameter(property = "oss-upload.accessKeyId")
    private String accessKeyId;

    @Parameter(property = "oss-upload.accessKeySecret")
    private String accessKeySecret;

    @Parameter(property = "oss-upload.accessFile")
    private File accessFile;

    @Parameter(property = "oss-upload.bucketName", required = true)
    private String bucketName;

    @Parameter(property = "oss-upload.source", required = true)
    private File source;

    @Parameter(property = "oss-upload.destination", required = true)
    private String destination;

    public void execute() throws MojoExecutionException {
        if (!source.exists()) {
            throw new MojoExecutionException("File/folder doesn't exist: " + source);
        }
        AccessInfo accessInfo = getAccessKeyAndAccessValue();
        OSS ossClient = new OSSClientBuilder()
            .build(endpoint, accessInfo.getAccessKeyId(), accessInfo.getAccessKeySecret());
        if (!ossClient.doesBucketExist(bucketName)) {
            throw new MojoExecutionException("Bucket doesn't exist: " + bucketName);
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, destination, source);
        PutObjectResult result = ossClient.putObject(putObjectRequest);
        getLog().info(
            "Upload file to oss, result etag: " + result.getETag() + ", result versionId: "
                + result.getVersionId());

        ossClient.shutdown();
    }

    private AccessInfo getAccessKeyAndAccessValue() throws MojoExecutionException {
        if (accessKeyId != null && accessKeySecret != null) {
            return new AccessInfo(accessKeyId, accessKeySecret);
        }
        if (accessFile != null) {
            StringBuilder data = new StringBuilder();
            try {
                Scanner myReader = new Scanner(accessFile);
                while (myReader.hasNextLine()) {
                    data.append(myReader.nextLine());
                }
                myReader.close();
            } catch (FileNotFoundException ex) {
                throw new MojoExecutionException("Access file doesn't exist: " + accessFile);
            }
            return AccessInfo.fromJson(data.toString());
        }
        throw new MojoExecutionException("Access info is empty.");
    }

    public static class AccessInfo {

        private final static Gson gson = new Gson();

        private String accessKeyId;

        private String accessKeySecret;

        public AccessInfo() {

        }

        public AccessInfo(String accessKeyId, String accessKeySecret) {
            this.accessKeyId = accessKeyId;
            this.accessKeySecret = accessKeySecret;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public static AccessInfo fromJson(String jsonString) {
            return gson.fromJson(jsonString, AccessInfo.class);
        }
    }

}
