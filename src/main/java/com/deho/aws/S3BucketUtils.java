package com.deho.aws;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3BucketUtils {

	public static AmazonS3 getS3Client() {
		return AmazonS3ClientBuilder.defaultClient();
	}

	public static AmazonS3 getS3Client(Regions clientRegion) {
		return AmazonS3ClientBuilder.standard().withRegion(clientRegion).withCredentials(new ProfileCredentialsProvider()).build();
	}

	public static void listBuckets() {
		listBuckets(getS3Client());
	}

	public static void listBuckets(Regions region) {
		listBuckets(getS3Client(region));
	}

	public static void listBuckets(AmazonS3 s3) {
		List<Bucket> buckets = s3.listBuckets();
		buckets.stream().forEach(System.out::println);
	}

	public static Bucket createBucket(Regions region, String bucketName) {
		return createBucket(getS3Client(region), bucketName);
	}

	public static Bucket createBucket(String bucketName) {
		return createBucket(getS3Client(), bucketName);
	}

	public static Bucket createBucket(AmazonS3 s3, String bucketName) {
		Bucket b = null;
		if (s3.doesBucketExistV2(bucketName)) {
			System.out.format("Bucket %s already exists.\n", bucketName);
			b = getBucket(bucketName);
		} else {
			try {
				b = s3.createBucket(bucketName);
			} catch (AmazonS3Exception e) {
				e.printStackTrace();
				System.err.println(e.getErrorMessage());
			}
		}
		return b;
	}

	public static Bucket getBucket(Regions region, String bucketName) {
		return getBucket(getS3Client(region), bucketName);
	}

	public static Bucket getBucket(String bucketName) {
		return getBucket(getS3Client(), bucketName);
	}

	public static Bucket getBucket(AmazonS3 s3, String bucket_name) {
		Bucket namedBucket = null;
		List<Bucket> buckets = s3.listBuckets();
		for (Bucket b : buckets) {
			if (b.getName().equals(bucket_name)) {
				namedBucket = b;
			}
		}
		return namedBucket;
	}

	public static void uploadFile(Regions region, String bucketName, String fileObjKeyName, String filePath) {
		uploadFile(getS3Client(region), bucketName, fileObjKeyName, filePath);
	}

	public static void uploadFile(String bucketName, String fileObjKeyName, String filePath) {
		uploadFile(getS3Client(), bucketName, fileObjKeyName, filePath);
	}

	public static void uploadFile(AmazonS3 s3, String bucketName, String fileObjKeyName, String filePath) {
		try {
			// Upload a file as a new object with ContentType and title specified.
			PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(filePath));
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("plain/text");
//            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
			request.setMetadata(metadata);
			s3.putObject(request);
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
			e.printStackTrace();
			throw e;
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
			e.printStackTrace();
			throw e;
		}
	}

	public static void uploadTextFile(Regions region, String bucketName, String stringObjKeyName, String text) {
		uploadTextFile(getS3Client(region), bucketName, stringObjKeyName, text);
	}

	public static void uploadTextFile(String bucketName, String stringObjKeyName, String text) {
		uploadTextFile(getS3Client(), bucketName, stringObjKeyName, text);
	}

	public static void uploadTextFile(AmazonS3 s3, String bucketName, String stringObjKeyName, String text) {
		try {
			// Upload a text string as a new object.
			s3.putObject(bucketName, stringObjKeyName, text);
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
			e.printStackTrace();
			throw e;
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
			e.printStackTrace();
			throw e;
		}
	}

	public static S3Object getObject(String bucketName, String key) throws IOException {
		return getObject(getS3Client(), bucketName, key);
	}

	public static S3Object getObject(Regions region, String bucketName, String key) throws IOException {
		return getObject(getS3Client(region), bucketName, key);
	}

	public static S3Object getObject(AmazonS3 s3, String bucketName, String key) throws IOException {
		S3Object fullObject = null;
		try {
			fullObject = s3.getObject(new GetObjectRequest(bucketName, key));

			displayTextInputStream(fullObject);

			return fullObject;

		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
			e.printStackTrace();
			throw e;
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
			e.printStackTrace();
			throw e;
		} finally {
			// To ensure that the network connection doesn't remain open, close any open input streams.
			if (fullObject != null) {
				fullObject.close();
			}
		}
	}

	public static String getS3ObjectContents(S3Object s3Object) throws IOException {
		try (S3ObjectInputStream objectInputStream = s3Object.getObjectContent()) {
			String text = IOUtils.toString(objectInputStream.getDelegateStream(), StandardCharsets.UTF_8);
			return text;
		}
	}

	public static void displayTextInputStream(S3Object s3Object) throws IOException {
		String text = getS3ObjectContents(s3Object);
		System.out.println(text);
	}
}
