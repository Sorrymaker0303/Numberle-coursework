import Controller.NumberleController;
import Model.INumberleModel;
import Model.NumberleModel;
import View.MyGUI;
import View.NumberleView;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;

public class GUIApp {
    public static void main(String[] args) {



        javax.swing.SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        try {
//                            BeautyEyeLNFHelper.frameBorderStyle = org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
                            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
                            UIManager.put("RootPane.setupButtonVisible",false);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        MyGUI myGUI = new MyGUI();
//                        createAndShowGUI();
                    }
                }
        );
    }


}