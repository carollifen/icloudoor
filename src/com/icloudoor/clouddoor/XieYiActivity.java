package com.icloudoor.cloudoor;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class XieYiActivity extends Activity {

	private RelativeLayout back;
	static Point size;
	static float density;
	
	private RelativeLayout content;
	private TextView textview1;
	private TextView tishi;
	private TextView fuwuneirong;
	private TextView zhuceziliao;
	private TextView zhanghaomima;
	private TextView quanli;
	private TextView yiwu;
	private TextView baohu;
	private TextView mianze;
	private TextView zhongzhi;
	private TextView peichang;
	private TextView guanggao;
	private TextView disanfang;
	private TextView shiyong;
	private TextView jieshiquan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xie_yi);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		//
		Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        density = dm.density;
        display.getSize(size);

        content = (RelativeLayout) findViewById(R.id.content);
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) content.getLayoutParams();
        param.width = dm.widthPixels - 64;
        content.setLayoutParams(param);
        
        textview1 = (TextView) findViewById(R.id.textView1);
        textview1.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本协议是由用户（以下简称为“您”）与广州非攻信息科技有限公司（以下简称为“我们”）"
        		+ "就我们所提供的产品和服务所订立的协议。"));
        
        tishi = (TextView) findViewById(R.id.tishi);
        tishi.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在您开始注册程序使用云门开门服务前，您应当具备中华人民共和国法律规定的与您行为相适应的民事行为能力。"
        		+ "<b>若您不具备前述与您行为相适应的民事行为能力，则您及您的监护人应依照法律规定承担因此而导致的一切后果。</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为保障您的权益，您在申请注册流程中点击同意本协议之前，应当认真阅读本协议。"
        		+ "<b>请您务必审慎阅读、充分理解各条款内容，特别是免除或者限制责任的条款、法律适用和争议解决条款。免除或者限制责任的条款将以粗体标识，您应重点阅读。</b>"
        		+ "如果您获得并使用我们所提供的产品和服务，则应视为您已经详细阅读了本协议的内容，同意本协议的内容，并同意遵守本协议的规定。"));
                
        fuwuneirong = (TextView) findViewById(R.id.fuwuneirong);
        fuwuneirong.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.云门开门服务的具体内容由我们根据实际情况提供，例如：优惠信息、服务类别、物业公告等。<b>我们保留随时变更、中断或终止部分或全部服务的权利。</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.我们仅免费为您提供相关的开门服务，除此之外所需的费用（如网络购物费、流量费等）均应由您自行负担。"));
        
        zhuceziliao = (TextView) findViewById(R.id.zhuceziliao);
        zhuceziliao.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.在申请云门开门账号时，您向我们提供的是真实、详细及准确的个人资料；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.所有由您提供的个人资料将被我们用来作为识别您和其他用户的依据；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.<b>如果您所提供的个人注册资料与事实不符，或已变更而未及时更新，或有任何误导之嫌，导致我们无法为您提供进一步提供产品和服务，我们将不承担任何责任。</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4.您可以在任何时候通过我们提供的会员服务或我们公布的其他途径，更新或修改您申请注册时所提供的资料。"));
        
        
        zhanghaomima = (TextView) findViewById(R.id.zhanghaomima);
        zhanghaomima.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;此处指的是您注册成功的账号及密码（包括手势密码）。请了解账号在注册之后不可变更，而密码可以通过我们提供的服务进行修改。"
        		+ "您对于您的账号及密码的保管以及使用该账号和密码所进行的一切行动负有完全的责任。请不要将账号、密码转让或出借给他人使用。"
        		+ "<b>因为您的保管疏忽或其他任何个人行为导致您的账号或密码遭他人非法使用及因此所衍生的任何后果，我们不承担任何责任。</b>"));
        
        quanli = (TextView) findViewById(R.id.quanli);
        quanli.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.您可以根据本协议以及我们不时更新和公布的其他规则使用我们提供的产品和服务。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.您有权在使用我们提供的产品和服务期间监督我们及我们的工作人员是否按照我们所公布的标准向您提供产品和服务，也可以随时向我们提出与我们的产品和服务有关的意见和建议。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.如果您不同意本协议条款，或对我们后来更新的条款有异议，或对我们所提供的产品和服务不满意，您可以选择停止使用我们的产品和服务。"
        		+ "<b>如果您选择停止使用我们的产品和服务，我们不再对您承担任何义务和责任。</b>"));
        
        yiwu = (TextView) findViewById(R.id.yiwu);
        yiwu.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.您同意按照我们不时发布、变更和修改的本协议条款及其他规则接受并使用我们的产品和服务，您不得通过不正当的手段或其他不公平的手段使用我们的产品和服务或参与我们活动。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.您不得干扰我们正常地提供产品和服务，包括但不限于：<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（1）攻击、侵入我们的网站服务器；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（2）破解、修改我们提供的客户端程序；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（3）攻击、侵入我们的服务器或服务器端程序；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（4）制作、使用、发布、传播任何形式的妨碍我们产品的辅助工具；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（5）利用程序的漏洞和错误（Bug）破坏我们产品的正常运行；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（6）不合理地干扰或阻碍他人使用我们所提供的产品和服务。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.<b>您必须保管好自己的账号和密码，由于您的原因导致账号和密码泄密而造成的后果均将由您自行承担。</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4.您仅能以一个单独的个人的身份使用我们所提供的产品和服务，您不能利用我们所提供的产品和服务从事商业目的的活动，也不能利用我们的产品和服务进行销售或其他商业目的的活动。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5.您应对自己账号中的所有活动和事件负责。您应遵守有关互联网信息发布的有关法律、法规及通常适用的互联网一般道德和礼仪的规范，您将自行承担您所发布的信息内容所产生的责任。特别强调，您不得发布下列内容：<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（1）反对宪法所确定的基本原则的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（2）危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（3）损害国家荣誉和利益的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（4）煽动民族仇恨、民族歧视，破坏民族团结的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（5）破坏国家宗教政策，宣扬邪教和封建迷信的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（6）散布谣言，扰乱社会秩序，破坏社会稳定的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（7）散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（8）侮辱或者诽谤他人，侵害他人合法权益的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（9）含有法律、行政法规禁止的其他内容的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（10）侵犯任何第三者的知识产权，版权或公众/私人权利的；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（11）违反人文道德、风俗习惯的。"));
        
        baohu = (TextView) findViewById(R.id.baohu);
        baohu.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们非常重视您个人信息及隐私的保护，在您使用我们提供的服务时，您同意我们按照本协议的约定收集、储存、使用、披露和保护您的个人信息。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们对您的个人信息承担保密义务，不会为满足第三方的营销目的而向其出售或出租您的任何信息，我们会在下列情况下才将您的个人信息与第三方共享：<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.事先获得您的同意或授权。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.根据法律法规的规定或行政或司法机构的要求。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.向我们的关联方分享您的个人信息。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4.向可信赖的合作伙伴提供您的个人信息，让他们根据我们的指示并遵循我们的隐私权政策以及其他任何相应的保密和安全措施来为我们处理这些信息。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5.为保护我们的知识产权和其他财产权益，需要披露您的个人资料。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6.只有共享您的信息，才能提供您需要的服务，或处理您与他人的纠纷或争议。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.如您出现违反中国有关法律、法规或者本协议的情况，需要向第三方披露。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8.为维护其他用户的合法权益。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们将采取商业上合理的方式以保护您的个人信息的安全，我们将使用通常可以获得的安全技术和程序来保护您的个人资料不被未经授权的访问、使用或泄漏。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>在不透露单个用户隐私资料的前提下，我们有权对整个用户数据库进行技术分析并对已进行分析、整理后的用户数据库进行商业上的利用。</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>尽管对用户的隐私权保护做了极大的努力，但是仍然不能保证现有的安全技术措施使用户的技术信息等不受任何形式的损失。</b>"));
        
        mianze = (TextView) findViewById(R.id.mianze);
        mianze.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>1.我们不对我们提供的服务进行任何形式的保证，包括但不限于以下事宜：<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（1）本服务将符合您的需求；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（2）本服务将不受干扰、及时提供、安全可靠或不会出错。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.您已经了解并同意对于因我们、我们的合作方或网络系统软硬件设备的故障、失灵或人为操作的疏失而全部或部分中断、暂时无法使用、迟延并造成我们提供产品和服务的停止或中断的，我们不承担任何责任。对于因他人侵入我们的网络或系统，篡改、删改或伪造、变造网站和用户资料或数据而造成我们的产品和服务的停止或中断，我们也不承担任何责任。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.我们依法律规定承担基础保障义务，但无法对由于信息网络设备维护、连接故障，电脑、通讯或其他系统的故障，电力故障，罢工，暴乱，火灾，洪水，风暴，爆炸，战争，政府行为，司法行政机关的命令或因第三方原因而给您造成的损害结果承担责任。</b>"));
        
        zhongzhi = (TextView) findViewById(R.id.zhongzhi);
        zhongzhi.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>1.如因系统维护或升级的需要而需暂停网络服务，我们将尽可能事先进行通告。在此情形下，我们不承担任何责任。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.您应确实遵守本协议及有关法律法规的规定，我们对于您是否违反本协议拥有最终认定权。如发生下列任何一种情形，我们有权随时中断或终止向您提供本协议项下的网络服务而无需通知，同时立即暂停及终止或删除您账号与您账号中的所有相关资料、档案及任何记录，以及取消、停止、限制您的会员资格：<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（1）您提供的个人资料不真实；<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（2）您违反法律法规或本协议的任何约定。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.除前款所述情形外，我们保留在不事先通知您的情况下随时中断或终止部分或全部网络服务的权利，对于所有服务的中断或终止而造成的任何损失，无需对您或任何第三方承担任何责任。</b>"));
        
        peichang = (TextView) findViewById(R.id.peichang);
        peichang.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您若违反本协议或相关法律法规，导致我们的关联企业、受雇人、代理人及其他一切相关履行辅助人员因此受到损害或支出一切衍生费用（包括但不限于支付上述人士须就您的违法行为所进行的一切辩护或索偿诉讼及相关和解之法律费用），您应承担相应的违约责任及损害赔偿责任。"));
        
        guanggao = (TextView) findViewById(R.id.guanggao);
        guanggao.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们可能刊登商业广告或其它活动促销的广告。这些内容系广告商或商品服务提供者所为，我们仅提供刊登内容的媒介。您通过我们或我们所链接的网站所购买的服务或商品，其交易行为仅存于您与该商品或服务的提供者之间，与我们无关。"));
        
        disanfang = (TextView) findViewById(R.id.disanfang);
        disanfang.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您可能在使用我们的产品和服务过程中链接到第三方的站点。第三方的站点不由我们控制，并且我们也不对任何第三方站点的内容、第三方站点包含的任何链接、或第三方站点的任何更改或更新负责。"
        		+ "<b>我们仅为了提供便利的目的而向您提供这些到第三方站点的链接，我们所提供的这些链接并不意味着我们认可该第三方站点。您需要检查并遵守该第三方站点的相关规定。</b>"));
        
        shiyong = (TextView) findViewById(R.id.shiyong);
        shiyong.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本协议之订立、生效、解释、修订、补充、终止、执行与争议解决均适用中华人民共和国大陆地区法律；如法律无相关规定的，参照商业惯例及/或行业惯例。<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您因使用我们的产品和服务产生的任何争议，我们与您协商解决。协商不成的，任一方有权向我公司所在地人民法院起诉。"));
        
        jieshiquan = (TextView) findViewById(R.id.jieshiquan);
        jieshiquan.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;基于对我们本身、用户及市场状况不断变化的考虑，我们保留随时修改、新增、删除本协议条款的权利，修改、新增、删除本协议条款时，我们将于官方网站首页公告修改、新增、删除的事实，而不另行对您进行个别通知。"
        		+ "<b>若您不同意我们所修改、新增、删除的内容，可停止使用我们所提供的服务。若您继续使用我们的服务，则视同您同意并接受本协议条款修改、新增、删除后之一切内容，且不得因此而要求任何补偿或赔偿。</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们保留对本协议条款、产品和服务以及我们所提供的产品和服务的相关官方网站的最终解释权。"));
        
        new Thread(){
        	
        	@Override
			public void run() {
        		TextJustification.justify(textview1, size.x - 64);
        		TextJustification.justify(tishi, size.x - 64);
        		TextJustification.justify(fuwuneirong, size.x - 64);
        		TextJustification.justify(zhuceziliao, size.x - 64);
        		TextJustification.justify(zhanghaomima, size.x - 64);
        		TextJustification.justify(quanli, size.x - 64);
        		TextJustification.justify(yiwu, size.x - 64);
        		TextJustification.justify(baohu, size.x - 64);
        		TextJustification.justify(mianze, size.x - 64);
        		TextJustification.justify(zhongzhi, size.x - 64);
        		TextJustification.justify(peichang, size.x - 64);
        		TextJustification.justify(guanggao, size.x - 64);
        		TextJustification.justify(disanfang, size.x - 64);
        		TextJustification.justify(shiyong, size.x - 64);
        		TextJustification.justify(jieshiquan, size.x - 64);
        	}
        }.start();
        
	}

}
