# Oss-upload-maven-plugin

Uploads a file to aliyun OSS.

## Configuration parameters

| Parameter | Description | Required | Default |
| ----- | ---- | ---- | ---- |
| endpoint | Oss endpoint | yes | http://oss-cn-hangzhou.aliyuncs.com |
| accessKeyId | Oss access key | no | |
| accessKeySecret | Oss access value | no | |
| accessFile | Oss access info file | no | |
| bucketName | The name of the bucket | yes | |
| source | The source file | yes | | 
| destination | The destination file | yes | |

## Access file example

```json
{
  "accessKeyId": "test",
  "accessKeySecret": "test"
}
```

## Access info chain

+ Get from `ACCESS_KEY_ID`,`ACCESS_KEY_SECRET` in `System.env`
+ Get from `accessKeyId`, `accessKeySecret` in Configuration
+ Get from `accessFile` in Configuration

## Upload a file example

```xml
<plugin>
  <groupId>com.github.talkwithkeyboard</groupId>
  <artifactId>oss-upload-maven-plugin</artifactId>
  <version>1.0.1</version>
  <configuration>
    <endpoint>http://oss-cn-hangzhou.aliyuncs.com</endpoint>
    <accessKeyId>test</accessKeyId>
    <accessKeySecret>test</accessKeySecret>
    <bucketName>my-oss-bucket</bucketName>
    <source>dir/filename.txt</source>
    <destination>remote-dir/remote-filename.txt</destination>
  </configuration>
</plugin>
```

```
mvn oss-upload:oss-upload
```