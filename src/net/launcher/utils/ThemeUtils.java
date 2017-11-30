package net.launcher.utils;

import static net.launcher.theme.DraggerTheme.dbuttons;
import static net.launcher.theme.DraggerTheme.dragger;
import static net.launcher.theme.DraggerTheme.title;
import static net.launcher.theme.LoginTheme.frameH;
import static net.launcher.theme.LoginTheme.frameW;
import static net.launcher.theme.LoginTheme.links;
import static net.launcher.theme.LoginTheme.login;
import static net.launcher.theme.LoginTheme.newsBrowser;
import static net.launcher.theme.LoginTheme.password;
import static net.launcher.theme.LoginTheme.serverbar;
import static net.launcher.theme.LoginTheme.servers;
import static net.launcher.theme.LoginTheme.toAuth;
import static net.launcher.theme.LoginTheme.toGame;
import static net.launcher.theme.LoginTheme.toLogout;
import static net.launcher.theme.LoginTheme.toOptions;
import static net.launcher.theme.LoginTheme.toPersonal;
import static net.launcher.theme.LoginTheme.toRegister;
import static net.launcher.theme.LoginTheme.update_exe;
import static net.launcher.theme.LoginTheme.update_jar;
import static net.launcher.theme.LoginTheme.update_no;
import static net.launcher.theme.OptionsTheme.Music;
import static net.launcher.theme.OptionsTheme.cleandir;
import static net.launcher.theme.OptionsTheme.close;
import static net.launcher.theme.OptionsTheme.fullscrn;
import static net.launcher.theme.OptionsTheme.loadnews;
import static net.launcher.theme.OptionsTheme.memory;
import static net.launcher.theme.OptionsTheme.updatepr;
import static net.launcher.theme.PersonalTheme.buyCloak;
import static net.launcher.theme.PersonalTheme.buyPremium;
import static net.launcher.theme.PersonalTheme.buyUnban;
import static net.launcher.theme.PersonalTheme.buyVaucher;
import static net.launcher.theme.PersonalTheme.buyVip;
import static net.launcher.theme.PersonalTheme.changeskin;
import static net.launcher.theme.PersonalTheme.exchangeBtn;
import static net.launcher.theme.PersonalTheme.exchangeFrom;
import static net.launcher.theme.PersonalTheme.exchangeTo;
import static net.launcher.theme.PersonalTheme.toGamePSL;
import static net.launcher.theme.PersonalTheme.vaucher;
import static net.launcher.theme.PersonalTheme.vaucherButton;
import static net.launcher.theme.RegTheme.closereg;
import static net.launcher.theme.RegTheme.loginReg;
import static net.launcher.theme.RegTheme.mailReg;
import static net.launcher.theme.RegTheme.okreg;
import static net.launcher.theme.RegTheme.password2Reg;
import static net.launcher.theme.RegTheme.passwordReg;

import java.awt.Dimension;
import java.awt.FontMetrics;

import net.launcher.components.Frame;
import net.launcher.components.LinkLabel;
import net.launcher.run.Settings;

public class ThemeUtils extends BaseUtils {
	public static void updateStyle(Frame main) throws Exception {
		int i = 0;
		for (LinkLabel link : main.links) {
			links.apply(link);
			FontMetrics fm = link.getFontMetrics(link.getFont());
			link.setBounds(i + links.x, links.y, fm.stringWidth(link.getText()), fm.getHeight());
			i += fm.stringWidth(link.getText()) + links.margin;
		}

		title.apply(main.title);
		dragger.apply(main.dragger);
		dbuttons.apply(main.hide, main.close);
		toGame.apply(Frame.toGame);
		toAuth.apply(Frame.toAuth);
		toLogout.apply(Frame.toLogout);
		toPersonal.apply(Frame.toPersonal);
		toRegister.apply(Frame.toRegister);
		toOptions.apply(main.toOptions);
		login.apply(Frame.login);
		password.apply(Frame.password);
		servers.apply(main.servers);
		serverbar.apply(main.serverbar);
		loadnews.apply(main.loadnews);
		Music.apply(main.Music);
		updatepr.apply(main.updatepr);
		cleandir.apply(main.cleanDir);
		fullscrn.apply(main.fullscreen);
		memory.apply(main.memory);
		close.apply(main.options_close);
		closereg.apply(main.closereg);
		loginReg.apply(main.loginReg);
		passwordReg.apply(main.passwordReg);
		password2Reg.apply(main.password2Reg);
		mailReg.apply(main.mailReg);
		okreg.apply(main.okreg);
		buyCloak.apply(main.buyCloak);
		changeskin.apply(main.changeSkin);
		buyVip.apply(main.buyVip);
		buyPremium.apply(main.buyPremium);
		buyUnban.apply(main.buyUnban);
		vaucher.apply(main.vaucher);
		vaucherButton.apply(main.vaucherButton);
		buyVaucher.apply(main.buyVaucher);
		exchangeFrom.apply(main.exchangeFrom);
		exchangeTo.apply(main.exchangeTo);
		exchangeBtn.apply(main.exchangeButton);
		toGamePSL.apply(main.toGamePersonal);
		update_no.apply(main.update_no);
		update_exe.apply(main.update_exe);
		update_jar.apply(main.update_jar);
		newsBrowser.apply(main.bpane);
		main.panel.setPreferredSize(new Dimension(frameW, frameH));

		main.setIconImage(BaseUtils.getLocalImage("favicon"));
		main.setTitle(Settings.title);
		main.setLocationRelativeTo(null);
		main.pack();
		main.repaint();
	}
}