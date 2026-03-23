package whatsapp.web.cloudfare.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
public class CloudfareService {

    @Value("${spring.cloudfare.secret-key}")
    private String secretKey;

    @Value("${spring.cloudfare.access-key}")
    private String accessKey;

    @Value("${spring.cloudfare.r2-region}")
    private String r2Region;

    @Value("${spring.cloudfare.account-id}")
    private String accountId;

    private URI endpoint;

    private S3Client s3;

    @PostConstruct
    public void initCloudFareService() {
        this.endpoint = URI.create("https://" + this.accountId + ".r2.cloudflarestorage.com");
        this.s3 = S3Client.builder()
                .endpointOverride(this.endpoint)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(this.accessKey, this.secretKey))
                )
                .region(Region.of(this.r2Region))
                .build();
    }

    public Boolean createBucket(String newBucketName) {
        try {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(this.accessKey, this.secretKey);

            // Criar cliente para a API S3
            S3Client s3Client = S3Client.builder()
                    .region(Region.of(this.r2Region)) // Região do seu bucket
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .endpointOverride(this.endpoint)  // Cloudflare R2 endpoint
                    .build();

            // Criar o bucket
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(newBucketName)
                    .build();

            s3Client.createBucket(createBucketRequest);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String generateLinkFile(String bucketName, String fileName) throws S3Exception, IOException{
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(this.accessKey, this.secretKey);

        // Criando o cliente para a API S3
        S3Presigner s3Presigner = S3Presigner.builder()
                .region(Region.of(this.r2Region)) // Ajuste conforme a região do seu bucket
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(this.endpoint)
                .build();

        // Criar a solicitação para obter um objeto
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // Gerar o URL pré-assinado
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                presignedGetObjectRequest -> presignedGetObjectRequest.getObjectRequest(getObjectRequest)
                        .signatureDuration(Duration.ofMinutes(15)) // Link válido por 15 minutos
        );

        return presignedRequest.url().toString();
    }

    public Boolean doesBucketExist(String bucketName) {
        try {
            HeadBucketRequest headRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            HeadBucketResponse response = s3.headBucket(headRequest);
            return response != null;
        } catch (S3Exception e) {
            return false;
        }
    }

    public boolean isBucketEmpty(String bucketName) {
        try {
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsResponse listObjectsResponse = s3.listObjects(listObjectsRequest);

            // Se a lista de objetos estiver vazia, o bucket está vazio
            return listObjectsResponse.contents().isEmpty();
        } catch (S3Exception e) {
            return false; // Em caso de erro, consideramos que o bucket não está vazio
        }
    }

    public Boolean deleteBucket(String bucketName) {
        try {
            // Verifica se o bucket existe antes de tentar deletá-lo
            if (!doesBucketExist(bucketName)) {
                return false;
            }

            // Verifica se o bucket está vazio
            if (!isBucketEmpty(bucketName)) {
                return false;
            }

            // Cria o objeto DeleteBucketRequest
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Exclui o bucket
            s3.deleteBucket(deleteBucketRequest);

            return true;

        } catch (S3Exception e) {
            return false;
        }
    }


    public Boolean uploadFile(String bucketName, String fileName, InputStream fileContent,
                              String mimeType) {
        try {
            // Verifica se o bucket existe antes de tentar enviar o arquivo
            if (!doesBucketExist(bucketName)) {
                this.createBucket(bucketName);
            }

            // Cria o objeto PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName) // Nome do arquivo no bucket
                    .contentType(mimeType) // Adiciona o tipo MIME
                    .build();

            // Envia o arquivo para o bucket
            s3.putObject(putObjectRequest, RequestBody.fromInputStream(fileContent, fileContent.available()));

            return true;
        } catch (S3Exception | IOException e) {
            return false;
        }
    }

    public Boolean deleteFile(String bucketName, String fileName) {
        try {
            // Verifica se o bucket existe antes de tentar deletar o arquivo
            if (!doesBucketExist(bucketName)) {
                return false;
            }

            // Cria o objeto DeleteObjectRequest
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName) // Nome do arquivo a ser deletado
                    .build();

            // Deleta o arquivo do bucket
            s3.deleteObject(deleteObjectRequest);

            return true;

        } catch (S3Exception e) {
            return false;
        }
    }

    public ResponseEntity<?> listFiles(String bucketName) {
        try {
            // Verifica se o bucket existe antes de tentar listar os arquivos
            if (!doesBucketExist(bucketName)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Bucket not found: " + bucketName);
            }

            // Cria o objeto ListObjectsRequest
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Lista os objetos do bucket
            ListObjectsResponse listObjectsResponse = s3.listObjects(listObjectsRequest);

            // Obtém a lista de arquivos (objetos)
            List<S3Object> objects = listObjectsResponse.contents();

            if (objects.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Bucket is empty: " + bucketName);
            }

            // Se houver arquivos, retorna a lista
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(objects);

        } catch (S3Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error listing files: " + e.getMessage());
        }
    }
}
