package com.icloudoor.clouddoor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;

public class TakePicCamParaUtil {
	
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static TakePicCamParaUtil myCamPara = null;
	private TakePicCamParaUtil(){

	}
	public static TakePicCamParaUtil getInstance(){
		if(myCamPara == null){
			myCamPara = new TakePicCamParaUtil();
			return myCamPara;
		}
		else{
			return myCamPara;
		}
	}

	public  Size getPropPreviewSize(List<Camera.Size> list, float th, int minHeight){
		Collections.sort(list, sizeComparator);

		int i = 0;
		for(Size s:list){
			if((s.height >= minHeight) && equalRate(s, th)){
				break;
			}
			i++;
		}
		if(i == list.size()){
			i = 0;//如果没找到，就选最小的size
		}
		return list.get(i);
	}
	public Size getPropPictureSize(List<Camera.Size> list, float th, int minHeight){
		Collections.sort(list, sizeComparator);

		int i = 0;
		for(Size s:list){
			if((s.height >= minHeight) && equalRate(s, th)){
				break;
			}
			i++;
		}
		if(i == list.size()){
			i = 0;//如果没找到，就选最小的size
		}
		return list.get(i);
	}

	public boolean equalRate(Size s, float rate){
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.03)
		{
			return true;
		}
		else{
			return false;
		}
	}

	public  class CameraSizeComparator implements Comparator<Camera.Size>{
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
				return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}

	}

	/**打印支持的previewSizes
	 * @param params
	 */
	public  void printSupportPreviewSize(Camera.Parameters params){
		List<Size> previewSizes = params.getSupportedPreviewSizes();
		for(int i=0; i< previewSizes.size(); i++){
			Size size = previewSizes.get(i);
		}
	
	}

	/**打印支持的pictureSizes
	 * @param params
	 */
	public  void printSupportPictureSize(Camera.Parameters params){
		List<Size> pictureSizes = params.getSupportedPictureSizes();
		for(int i=0; i< pictureSizes.size(); i++){
			Size size = pictureSizes.get(i);
		}
	}
	/**打印支持的聚焦模式
	 * @param params
	 */
	public void printSupportFocusMode(Camera.Parameters params){
		List<String> focusModes = params.getSupportedFocusModes();
		for(String mode : focusModes){
		}
	}
}