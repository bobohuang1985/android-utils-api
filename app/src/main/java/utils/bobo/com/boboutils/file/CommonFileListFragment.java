package utils.bobo.com.boboutils.file;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bobo.commonutils.file.AesFileEncryptor;
import com.bobo.commonutils.file.EasyFileEncryptor;
import com.bobo.commonutils.file.FileEncryptor;
import com.bobo.commonutils.file.FileUtils;
import com.bobo.commonutils.inferface.UpdateProgressInferface;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import utils.bobo.com.boboutils.R;


public class CommonFileListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
	private static final int REQUESTCODE_PERMISSION_STORAGE = 0;
	private static final int MSG_SHOW_PROGRESS_DIALOG    = 1;
	private static final int MSG_HIDE_PROGRESS_DIALOG    = 2;
	private static final int MSG_UPDATE_PROGRESS_DIALOG    = 3;
	private static final int MSG_RELOAD_FILE_LISTS    = 4;

	private HashSet<String> selectPath = new HashSet<>();
	ListView mListView;
	Button mSwitchStorage;
	Button mUpDir;
	TextView mCurPathTextView;
	String mCurPath;
	private ArrayList<String> mFileOrDirNames = new ArrayList<String>();
	private HashSet<String> mSelFilePath = new HashSet<String>();
	private int mDirectoryCount = 0;
	private String mIntStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	private String mSecondarySdPath;
	private MyAdapter mMyAdapter;
	private boolean mInIntStorage =true;
	private static final String FILE_ENCRYPTOR_KEY = "AB1234567890";
	private FileEncryptor mAESFileEncryptor = new AesFileEncryptor();
	private FileEncryptor mFastFileEncryptor = new EasyFileEncryptor();
	private ProgressDialog mProgressDialog;
	public static CommonFileListFragment newInstance() {
		CommonFileListFragment f = new CommonFileListFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_common_file_list,container,false);
		mSwitchStorage = (Button)rootView.findViewById(R.id.switchStorage);
		mUpDir = (Button)rootView.findViewById(R.id.upDir);
		mUpDir.setOnClickListener(this);
		mCurPathTextView = (TextView)rootView.findViewById(R.id.curPath);
		mListView = (ListView)rootView.findViewById(R.id.listView);
		mSecondarySdPath = getExtStorageSearch();
		mCurPath = mIntStoragePath;
		if(TextUtils.isEmpty(mSecondarySdPath)){
			mSwitchStorage.setEnabled(false);
			//mSwitchStorage.setVisibility(View.GONE);
		}else{
			mSwitchStorage.setEnabled(true);
			//mSwitchStorage.setVisibility(View.VISIBLE);
			mSwitchStorage.setOnClickListener(this);
		}
		mMyAdapter = new MyAdapter();
		mListView.setAdapter(mMyAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		this.requestPermissions(new String[]{
				Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_PERMISSION_STORAGE);
		return rootView;
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResulits){
		if(REQUESTCODE_PERMISSION_STORAGE == requestCode){
			for(int result:grantResulits){
				if(result != PackageManager.PERMISSION_GRANTED){
					getActivity().finish();
					return;
				}
			}
			reloadFileLists();
		}
	}
	private void reloadFileLists(){
		mFileOrDirNames.clear();
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		FileFilter directoryFilter = new FileFilter(){
			public boolean accept(File file){
				return file.isDirectory();
			}
		};

		//if(mainPath.exists() && mainPath.length()>0){
		//Lista de directorios
		File[] tempDirectoryList = new File(mCurPath).listFiles(directoryFilter);
		if(tempDirectoryList == null){
			return;
		}
		mDirectoryCount = tempDirectoryList.length;
		if (tempDirectoryList != null && tempDirectoryList.length > 1) {
			Arrays.sort(tempDirectoryList, new Comparator<File>() {
				@Override
				public int compare(File object1, File object2) {
					return object1.getName().compareTo(object2.getName());
				}
			});
		}

		for(File file: tempDirectoryList){
			mFileOrDirNames.add(file.getName());
		}

		//Lista de ficheros
		File[] tempFileList = new File(mCurPath).listFiles(fileFilter);

		if (tempFileList != null && tempFileList.length > 1) {
			Arrays.sort(tempFileList, new Comparator<File>() {
				@Override
				public int compare(File object1, File object2) {
					return object1.getName().compareTo(object2.getName());
				}
			});
		}

		for(File file : tempFileList){
			mFileOrDirNames.add(file.getName());
		}
		mMyAdapter.notifyDataSetChanged();
		mCurPathTextView.setText(mCurPath);
	}
	public String getExtStorageSearch(){
		String[] extStorlocs = {"/storage/sdcard1","/storage/extsdcard","/storage/sdcard0/external_sdcard","/mnt/extsdcard",
				"/mnt/sdcard/external_sd","/mnt/external_sd","/mnt/media_rw/sdcard1","/removable/microsd","/mnt/emmc",
				"/storage/external_SD","/storage/ext_sd","/storage/removable/sdcard1","/data/sdext","/data/sdext2",
				"/data/sdext3","/data/sdext4","/storage/sdcard0"};

		//First Attempt
		//primary_sd = System.getenv("EXTERNAL_STORAGE");
		String secondarySdPath = System.getenv("SECONDARY_STORAGE");

		if(mSecondarySdPath == null) {//if fail, search among known list of extStorage Locations
			for(String string: extStorlocs){
				if((new File(string)).exists() && (new File(string)).isDirectory() ){
					secondarySdPath = string;
					break;
				}
			}
		}
		return secondarySdPath;
	}
	@Override
	public void onClick(View v) {
		if (R.id.switchStorage == v.getId()) {
			mInIntStorage = !mInIntStorage;
			mCurPath = mInIntStorage?mIntStoragePath:mSecondarySdPath;
			reloadFileLists();
		}else if(R.id.upDir == v.getId()){
			if(mCurPath.equals(mIntStoragePath)||mCurPath.equals(mSecondarySdPath)){
				return;
			}
			mCurPath = new File(mCurPath).getParentFile().getPath();
			reloadFileLists();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String curFile = mCurPath+File.separator+mFileOrDirNames.get(position);
		if(position<mDirectoryCount){
			mCurPath = curFile;//mCurPath+File.separator+mFileOrDirNames.get(position);
			mCurPathTextView.setText(mCurPath);
			reloadFileLists();
		}else{
			/*if(mSelFilePath.contains(curFile)){
				mSelFilePath.remove(curFile);
			}else{
				mSelFilePath.add(curFile);
			}
			mMyAdapter.notifyDataSetChanged();*/

		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		String curFile = mCurPath+File.separator+mFileOrDirNames.get(position);
		if(position<mDirectoryCount){
			return false;
		}
		showFileOperationMenus(curFile);
		return true;
	}
	private void showFileOperationMenus(final String filePath){
		CharSequence[] items =  new CharSequence[]{getText(R.string.aes_encrypt),
				getText(R.string.aes_decrypt),
				getText(R.string.fast_encrypt),
				getText(R.string.fast_decrypt)};
		final int AES_ENCRYPT = 0;
		final int AES_DECRYPT = 1;
		final int FAST_ENCRYPT = 2;
		final int FAST_DECRYPT = 3;
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
		final String fileName = FileUtils.getFileName(filePath);
		builder.setTitle(fileName).setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, final int which) {
				new Thread() {
					@Override
					public void run() {
						if (AES_ENCRYPT == which) {
							Message msg = mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, "0%");
							mHandler.sendMessage(msg);
							String newFileName = FileUtils.getFileNameWithoutSuffix(fileName) + "_aesE" + FileUtils.getFileSuffix(fileName);
							newFileName = FileUtils
									.getNewFileAvailableName(
											new File(filePath).getParentFile().getPath(),
											newFileName);
							mAESFileEncryptor.encryptFile(filePath, new File(filePath).getParentFile().getPath() + File.separator + newFileName, FILE_ENCRYPTOR_KEY, mProgressInferface);
						} else if (AES_DECRYPT == which) {
							Message msg = mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, "0%");
							mHandler.sendMessage(msg);
							String newFileName = FileUtils.getFileNameWithoutSuffix(fileName) + "_aesD" + FileUtils.getFileSuffix(fileName);
							newFileName = FileUtils
									.getNewFileAvailableName(
											new File(filePath).getParentFile().getPath(),
											newFileName);
							mAESFileEncryptor.decryptFile(filePath, new File(filePath).getParentFile().getPath() + File.separator + newFileName, FILE_ENCRYPTOR_KEY, mProgressInferface);
						} else if (FAST_ENCRYPT == which) {
							Message msg = mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, "0%");
							mHandler.sendMessage(msg);
							String newFileName = FileUtils.getFileNameWithoutSuffix(fileName) + "_fastE" + FileUtils.getFileSuffix(fileName);
							newFileName = FileUtils
									.getNewFileAvailableName(
											new File(filePath).getParentFile().getPath(),
											newFileName);
							mFastFileEncryptor.encryptFile(filePath, new File(filePath).getParentFile().getPath() + File.separator + newFileName, FILE_ENCRYPTOR_KEY, mProgressInferface);
						} else if (FAST_DECRYPT == which) {
							Message msg = mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, "0%");
							mHandler.sendMessage(msg);
							String newFileName = FileUtils.getFileNameWithoutSuffix(fileName) + "_fastD" + FileUtils.getFileSuffix(fileName);
							newFileName = FileUtils
									.getNewFileAvailableName(
											new File(filePath).getParentFile().getPath(),
											newFileName);
							mFastFileEncryptor.decryptFile(filePath, new File(filePath).getParentFile().getPath() + File.separator + newFileName, FILE_ENCRYPTOR_KEY, mProgressInferface);
						}
						Message msg = mHandler.obtainMessage(MSG_HIDE_PROGRESS_DIALOG, "0%");
						mHandler.sendMessage(msg);
						mHandler.sendEmptyMessage(MSG_RELOAD_FILE_LISTS);
					}
				}.start();
				dialog.dismiss();
			}
			}
		).create();
		builder.show();
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Activity activity = CommonFileListFragment.this.getActivity();
			if (activity == null) {
				return;
			}
			switch (msg.what) {
				case MSG_SHOW_PROGRESS_DIALOG: {
					if (mProgressDialog == null) {
						mProgressDialog = new ProgressDialog(activity);
						mProgressDialog.setCanceledOnTouchOutside(false);
						mProgressDialog.setCancelable(false);
					}
					if (mProgressDialog.isShowing()) {
						mProgressDialog.cancel();
					}
					mProgressDialog.setMessage((String) (msg.obj));
					mProgressDialog.show();
					break;
				}
				case MSG_HIDE_PROGRESS_DIALOG:
					if (mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.cancel();
					}
					break;
				case MSG_UPDATE_PROGRESS_DIALOG:
					if (mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.setMessage((String) (msg.obj));
					}
					break;
				case MSG_RELOAD_FILE_LISTS:
					reloadFileLists();
					break;
			}
		}
	};

	private UpdateProgressInferface mProgressInferface = new UpdateProgressInferface(){
		@Override
		public void progressUpdate(int percent, Object object) {
			Message msg = mHandler.obtainMessage(MSG_UPDATE_PROGRESS_DIALOG, String.valueOf(percent)+"%");
			mHandler.sendMessage(msg);
		}
	};
	private class MyAdapter extends BaseAdapter{
		LayoutInflater mInfater = CommonFileListFragment.this.getActivity().getLayoutInflater();
		@Override
		public int getCount() {
			return mFileOrDirNames.size();
		}

		@Override
		public Object getItem(int position) {
			return mFileOrDirNames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHold hold;
			if(convertView==null){
				convertView = mInfater.inflate(R.layout.common_file_list_item,null);
				hold = new ViewHold();
				hold.mFileIcon = (ImageView)convertView.findViewById(R.id.img);
				hold.mFileName = (TextView)convertView.findViewById(R.id.fileName);
				hold.mCheckIcon = (ImageView)convertView.findViewById(R.id.check);
				convertView.setTag(hold);
			}else{
				hold = (ViewHold) convertView.getTag();
			}
			if(position<mDirectoryCount){
				hold.mFileIcon.setImageResource(R.drawable.folder);
				hold.mCheckIcon.setVisibility(View.GONE);
			}else{
				hold.mFileIcon.setImageResource(R.drawable.document);
				if(mSelFilePath.contains(mCurPath+File.separator+mFileOrDirNames.get(position))){
					hold.mCheckIcon.setVisibility(View.VISIBLE);
					hold.mCheckIcon.setImageResource(R.drawable.check_on);
				}else{
					hold.mCheckIcon.setVisibility(View.GONE);
				}
			}
			hold.mFileName.setText(mFileOrDirNames.get(position));

			return convertView;
		}
	}
	private class ViewHold{
	    ImageView mFileIcon;
		TextView mFileName;
	    ImageView mCheckIcon;
	}
}
