package com.taku.safe.campus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.dialog.ChoiceDialog;
import com.taku.safe.entity.ChoiceDialogItem;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.utils.CompressImage;
import com.taku.safe.utils.GsonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

//import droidninja.filepicker.FilePickerBuilder;
//import droidninja.filepicker.FilePickerConst;


public class NewTucaoFragment extends BasePermissionFragment implements View.OnClickListener {
    public final static String TAG = NewTucaoFragment.class.getSimpleName();

    private UIHandler mHandler;

    private TextView tv_type;
    private EditText et_title;
    private EditText et_content;

    private static final int PIC_NUM = 5; //最多5张图片

    private static final int FROM_CAMERA = 50;// 拍照
    private static final int REQUEST_IMAGE = 51;// 仿微信图片选择器

    private String mCurrentPhotoPath;


    private LinearLayout h_linear;

    private ArrayList<String> photoOriginPaths = new ArrayList<>();
    private ArrayList<String> photoCompressPaths = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_tucao, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);
    }


    private void initTitle(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.to_rector);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    private void initView(View view) {
        tv_type = (TextView) view.findViewById(R.id.tv_type);

        et_title = (EditText) view.findViewById(R.id.et_title);
        et_content = (EditText) view.findViewById(R.id.et_content);

        h_linear = (LinearLayout) view.findViewById(R.id.linear_pic);

        view.findViewById(R.id.btn_commit).setOnClickListener(this);
        view.findViewById(R.id.tv_choice).setOnClickListener(this);
        view.findViewById(R.id.frame_pic).setOnClickListener(this);
    }


    public Intent dispatchTakePictureIntent(Context context) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File newFile = createImageFile();
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", newFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
            }
            return takePictureIntent;
        }
        return null;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                Log.e("TAG", "Throwing Errors....");
                throw new IOException();
            }
        }

        File image = new File(storageDir, imageFileName);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * 相机拍照
     */
    public void fromCamera() {
        try {
            Intent intent = dispatchTakePictureIntent(getActivity());
            if (intent != null)
                startActivityForResult(intent, FROM_CAMERA);
            else
                Toast.makeText(getActivity(), "没有相机的应用程序", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pickerImage() {
        /*FilePickerBuilder.getInstance().setMaxCount(PIC_NUM)
                .setSelectedFiles(photoOriginPaths)
                .setActivityTheme(R.style.FilePickerTheme)
                .enableCameraSupport(false)
                .showFolderView(false)
                .pickPhoto(this);*/

        MultiImageSelector.create()
                .showCamera(false) // show camera or not. true by default
                .count(PIC_NUM) // max select image size, 9 by default. used width #.multi()
                .multi() // multi mode, default mode;
                .origin(photoOriginPaths) // original select data set, used width #.multi()
                .start(this, REQUEST_IMAGE);
    }


    private void showImageDialog() {
        final ArrayList<ChoiceDialogItem> list = new ArrayList<>();

        list.add(new ChoiceDialogItem("相机", 0));
        list.add(new ChoiceDialogItem("相册", 1));

        new ChoiceDialog.Builder(getContext())
                .setList(list)
                .callBack(new ChoiceDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 0) {
                            fromCamera();
                        } else {
                            pickerImage();
                        }

                    }
                })
                .builder()
                .show();
    }


    private void showChoiceDialog() {
        final ArrayList<ChoiceDialogItem> list = new ArrayList<>();

        String[] types = getResources().getStringArray(R.array.response_type_str);
        for (int i = 0; i < types.length; i++) {
            list.add(new ChoiceDialogItem(types[i], i));
        }

        new ChoiceDialog.Builder(getContext())
                .setList(list)
                .callBack(new ChoiceDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        tv_type.setText(list.get(position).getTitle());
                        tv_type.setTag(list.get(position).getId());

                    }
                })
                .builder()
                .show();
    }


    private void sendReport() {
        String url = AppConfig.PRESIDENT_MSG_NEW;

        if (tv_type.getTag() == null) {
            Toast.makeText(getContext(), "必须选择类型", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = (int) tv_type.getTag();
        String title = et_title.getText().toString();
        String content = et_content.getText().toString();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "必须填写标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), "必须填写内容", Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        Map<String, Object> params = new HashMap<>();

        params.put("type", type);
        params.put("title", title);
        params.put("content", content);

        for (int i = 0; i < photoCompressPaths.size(); i++) {
            File file = new File(photoCompressPaths.get(i));
            if (file.exists()) {
                params.put("image" + (i + 1), file);
            }
        }

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在发送校长直通车，请稍后...");
        dialog.show();
        OkHttpConnector.httpPostMultipart(url, header, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, "发送校长直通车成功", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    if (baseBean != null && baseBean.getMsg() != null) {
                        Toast.makeText(mContext, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_choice:
                showChoiceDialog();
                break;
            case R.id.frame_pic:
                if (photoOriginPaths.size() >= PIC_NUM) {
                    Toast.makeText(getContext(), String.format(getString(R.string.pic_num), PIC_NUM), Toast.LENGTH_SHORT).show();
                } else {
                    showImageDialog();
                }
                break;
            case R.id.btn_commit:
                sendReport();
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    ArrayList<String> addPaths = new ArrayList<>();
                    for (String item : paths) {
                        if (!photoOriginPaths.contains(item)) {
                            photoOriginPaths.add(item);
                            addPaths.add(item);
                        }
                    }
                    new CompressAsyncTask().execute(addPaths.toArray(new String[addPaths.size()]));
                }
                break;*/
            case FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK /*&& data != null*/) {
                    photoOriginPaths.add(mCurrentPhotoPath);
                    new CompressAsyncTask().execute(new String[]{mCurrentPhotoPath});
                }
                break;
            case REQUEST_IMAGE:
                if (resultCode == Activity.RESULT_OK /*&& data != null*/) {
                    // Get the result list of select image paths
                    ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    ArrayList<String> addPaths = new ArrayList<>();
                    for (String item : paths) {
                        if (!photoOriginPaths.contains(item)) {
                            photoOriginPaths.add(item);
                            addPaths.add(item);
                        }
                    }
                    new CompressAsyncTask().execute(addPaths.toArray(new String[addPaths.size()]));
                }
                break;
        }
    }


    /**
     * 压缩图片
     */
    private class CompressAsyncTask extends AsyncTask<String[], Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String[]... params) {

            ArrayList<String> res = new ArrayList<>();
            for (String item : params[0]) {
                res.add(CompressImage.compressImage(item));
            }

            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<String> res) {
            for (String item : res) {
                if (!photoCompressPaths.contains(item)) {
                    photoCompressPaths.add(item);
                    Bitmap bitmap = BitmapFactory.decodeFile(item);

                    /*int height = mContext.getResources().getDimensionPixelOffset(R.dimen.h_scrollview_height);
                    int width = (height * bitmap.getWidth()) / bitmap.getHeight();*/

                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View view = inflater.inflate(R.layout.item_select_pic, null);

                    /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                    view.setLayoutParams(params);*/

                    ImageView imgContent = (ImageView) view.findViewById(R.id.img_content);
                    imgContent.setImageBitmap(bitmap);

                    ImageView imgClose = (ImageView) view.findViewById(R.id.img_close);
                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = h_linear.indexOfChild(view);
                            if (index >= 0) {
                                h_linear.removeViewAt(index);

                                int size = photoOriginPaths.size();
                                int position = size - 1 - index;

                                photoOriginPaths.remove(position);
                                photoCompressPaths.remove(position);
                            }
                        }
                    });
                    h_linear.addView(view, 0);
                }
            }

        }
    }

    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<NewTucaoFragment> mTarget;

        UIHandler(NewTucaoFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

}
