package com.applidium.nickelodeon.uitls;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.applidium.nickelodeon.config.AppConfig;


public class HttpConnector {

    private static final String TAG = HttpConnector.class.getSimpleName();

    private HttpConnector() {
        throw new AssertionError();
    }

    /**
     * AsyncTask to get data by HttpRequest
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
     * 2013-11-15
     */

    public static void httpGet(String url, IGetListener request) {
        new HttpGetAsyncTask(request).execute(url);
    }

    public static void httpGet(String url, ContentValues headers,
                               IGetListener request) {
        HttpGetAsyncTask task = new HttpGetAsyncTask(request);
        task.setHeaders(headers);
        task.execute(url);
    }


    public static void httpPost(String url, String postStr,
                                IPostListener request) {
        new HttpPostAsyncTask(request).execute(url, postStr);

    }

    public static void httpPost(String url, String postStr,
                                ContentValues headers,
                                IPostListener request) {
        HttpPostAsyncTask task = new HttpPostAsyncTask(request);
        task.setHeaders(headers);
        task.execute(url, postStr);
    }


    public static void httpPostFile(String url, Map<String, String> params,
                                    Map<String, File> files, IPostListener request) {
        new HttpPostFileAsyncTask(request, params, files).execute(url);

    }


    private static class HttpGetAsyncTask extends
            AsyncTask<String, Void, String> {

        private IGetListener mRequest;
        private ContentValues mHeaders;

        public HttpGetAsyncTask(IGetListener request) {
            mRequest = request;
        }

        public void setHeaders(ContentValues headers) {
            mHeaders = headers;
        }


        @Override
        protected String doInBackground(String... params) {

            String httpUrl = params[0];

            //是否走测试服务器
            if (AppConfig.TEST_HOST) {
                if (httpUrl.startsWith(AppConfig.REPORT_HOST)) {
                    httpUrl = httpUrl.replace(AppConfig.REPORT_HOST, AppConfig.REPORT_HOST_TEST);
                }

            }

            Log.v(TAG, "httpGet url =" + httpUrl);
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(httpUrl);
                urlConnection = (HttpURLConnection) url
                        .openConnection();

                if (mHeaders != null) {
                    for (Map.Entry<String, Object> entry : mHeaders.valueSet()) {
                        String key = entry.getKey(); // name
                        String value = entry.getValue().toString(); // value
                        urlConnection.setRequestProperty(key, value);
                    }
                }

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                String response = StreamUtils.readStream(in);

                return response;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }

            return null;

        }

        protected void onPostExecute(String response) {

            if (response == null) {
                mRequest.doError(new Exception("HttpGetAsyncTask error"));
                return;
            }
            try {
                Log.v(TAG, "httpGet response =" + response);

                mRequest.httpReqResult(response);
            } catch (Exception e) {
                mRequest.doError(e);
            }

        }

    }

    private static class HttpPostAsyncTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;
        private ContentValues mHeaders;

        public HttpPostAsyncTask(IPostListener request) {
            mRequest = request;
        }

        public void setHeaders(ContentValues headers) {
            mHeaders = headers;
        }

        @Override
        protected String doInBackground(String... params) {

            String httpUrl = params[0];
            String postStr = params[1];

            if (StringUtils.isEmpty(httpUrl)
                    || StringUtils.isEmpty(postStr)) {
                return null;
            }

            //是否走测试服务器
            if (AppConfig.TEST_HOST) {
                if (httpUrl.startsWith(AppConfig.MAIN_HOST)) {
                    httpUrl = httpUrl.replace(AppConfig.MAIN_HOST, AppConfig.MAIN_HOST_TEST);
                }

            }

            return doPostString(httpUrl, postStr, mHeaders);

        }

        protected void onPostExecute(String response) {

            if (StringUtils.isEmpty(response)) {
                mRequest.doError();
            } else {
                mRequest.httpReqResult(response);
            }

        }

    }


    private static class HttpPostFileAsyncTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;

        private Map<String, String> mParams;
        private Map<String, File> mFiles;

        public HttpPostFileAsyncTask(IPostListener request,
                                     Map<String, String> params,
                                     Map<String, File> files) {
            mRequest = request;
            mParams = params;
            mFiles = files;
        }


        @Override
        protected String doInBackground(String... params) {

            String httpUrl = params[0];

            if (StringUtils.isEmpty(httpUrl)) {
                return null;
            }

            return postFile(httpUrl, mParams, mFiles);

        }

        protected void onPostExecute(String response) {

            if (StringUtils.isEmpty(response)) {
                mRequest.doError();
            } else {
                mRequest.httpReqResult(response);
                if (mFiles != null) {
                    for (Map.Entry<String, File> file : mFiles.entrySet()) {
                        file.getValue().delete();
                    }
                }

            }

        }


    }


    /**
     * 发送请求
     *
     * @param path
     * @param strJson 请求参数
     * @return 请求结果
     */
    public static String doPostString(String path, String strJson, ContentValues headers) {
        String response = null;
        try {
            Log.v(TAG, path + " : post content = " + strJson);

            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(30000); // 链接超时
            connection.setReadTimeout(20000); // 读取超时

            connection.setDoOutput(true); // 是否输入参数
            connection.setDoInput(true); // 是否向HttpUrLConnection读入，默认true.
            connection.setRequestMethod("POST"); // 提交模式
            connection.setUseCaches(false); // 不能缓存
            connection.setInstanceFollowRedirects(true); // 连接是否尊重重定向
            connection.setRequestProperty("Content-Type", "application/json");

            if (headers != null) {
                for (Map.Entry<String, Object> entry : headers.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    connection.setRequestProperty(key, value);
                }
            }


            connection.connect(); // 连接，所有connection的配置信息必须在connect()方法前提交。

            OutputStream os = connection.getOutputStream();

            byte[] content = strJson.getBytes("utf-8");
            os.write(content, 0, content.length);
            os.flush();
            os.close();

            //服务器修改，错误类型封装到了 response json code里面，此处取消 http ResponseCode 判断
            int code = connection.getResponseCode();
            if (code == 200) {
                response = StreamUtils.readStream(connection.getInputStream());
            } else {
                response = StreamUtils.readStream(connection.getErrorStream());
            }

            Log.v(TAG, path + " : post response = " + response);
            connection.disconnect();

        } catch (IOException e) {
            Log.e(TAG, path + " : post Exception = " + e.toString());
            // e.printStackTrace();
        }
        return response;
    }

    /**
     * http post 文件
     *
     * @param url
     * @param params
     * @param files
     * @return
     * @throws IOException
     */
    public static String postFile(String url, Map<String, String> params, Map<String, File> files) {
        String response = null;

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        try {
            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(70 * 1000); // 缓存的最长时间
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);  // 不允许使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

            // 首先组拼文本类型的参数
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }

            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if (files != null) {
                for (Map.Entry<String, File> file : files.entrySet()) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey() + "\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());

                    InputStream is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    is.close();
                    outStream.write(LINEND.getBytes());
                }
            }
            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            // 得到响应码
            int code = conn.getResponseCode();
            if (code == 200) {
                response = StreamUtils.readStream(conn.getInputStream());
            } else {
                response = StreamUtils.readStream(conn.getErrorStream());
            }
            outStream.close();
            conn.disconnect();
        } catch (IOException e) {
            Log.e(TAG, url + " : post Exception = " + e.toString());
            //e.printStackTrace();
        }
        return response;
    }
}