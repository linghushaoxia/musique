/*
 * Copyright (c) 2008, 2009, 2010, 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.musique.gui.dialogs;

import com.tulskiy.musique.audio.AudioFileReader;
import com.tulskiy.musique.audio.player.io.AudioOutput;
import com.tulskiy.musique.gui.components.ColorChooser;
import com.tulskiy.musique.gui.components.FontChooser;
import com.tulskiy.musique.gui.language.LanguageConfigconst;
import com.tulskiy.musique.gui.language.LanguageUtil;
import com.tulskiy.musique.system.Application;
import com.tulskiy.musique.system.configuration.AlbumArtConfiguration;
import com.tulskiy.musique.system.configuration.Configuration;
import com.tulskiy.musique.system.configuration.LibraryConfiguration;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Author: Denis Tulskiy
 * Date: Jul 18, 2010
 */
@Deprecated
public class SettingsDialog extends JDialog {
    private Application app = Application.getInstance();
    private Configuration config = app.getConfiguration();

    private JButton applyButton;
    private JComponent owner;
    private JTabbedPane tabs;

    public SettingsDialog(JComponent owner) {
        super(SwingUtilities.windowForComponent(owner),LanguageUtil.getLocalText(LanguageConfigconst.SET_SETTINGS), ModalityType.MODELESS);
        this.owner = owner;

        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFocusable(false);

        Box buttons = Box.createHorizontalBox();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        applyButton = new JButton(LanguageUtil.getLocalText(LanguageConfigconst.SET_APPLY));

        final JButton cancelButton = new JButton(LanguageUtil.getLocalText(LanguageConfigconst.SET_CANCEL));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        JButton okButton = new JButton(LanguageUtil.getLocalText(LanguageConfigconst.SET_OK));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyButton.doClick();
                cancelButton.doClick();
            }
        });
        buttons.add(Box.createHorizontalGlue());
        buttons.add(okButton);
        buttons.add(Box.createHorizontalStrut(3));
        buttons.add(cancelButton);
        buttons.add(Box.createHorizontalStrut(3));
        buttons.add(applyButton);

        tabs.add(createSystemPanel());
        tabs.add(createNetworkPanel());
        tabs.add(createLibraryPanel());
        tabs.add(createGUIPanel());
        tabs.add(createColorsAndFontsPanel());
        add(tabs, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setSize(700, getHeight());
        setLocationRelativeTo(SwingUtilities.windowForComponent(owner));
    }

    public SettingsDialog(JComponent owner, String tabName) {
        this(owner);
        for (Component component : tabs.getComponents()) {
            if (component.getName().equals(tabName)) {
                tabs.setSelectedComponent(component);
                break;
            }
        }
    }

    private JComponent createGUIPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setName(LanguageUtil.getLocalText(LanguageConfigconst.SET_GUI));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 30));

        Box mainBox = Box.createVerticalBox();
        JPanel misc = new JPanel(new GridLayout(3, 2, 10, 10));
        mainBox.add(misc);

        misc.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_LOOK_AND_FEEL)));
        final UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        Vector<String> lafsVector = new Vector<String>();
        for (UIManager.LookAndFeelInfo laf : lafs) {
            lafsVector.add(laf.getName());
        }
        final JComboBox laf = new JComboBox(lafsVector);
        String name = UIManager.getLookAndFeel().getName();
        if (name.contains("GTK"))
            name = "GTK+";
        laf.setSelectedItem(name);
        misc.add(laf);

        final JCheckBox trayEnabled = new JCheckBox(LanguageUtil.getLocalText(LanguageConfigconst.SET_LIBRARY_ENABLE_NOTIFICATION_AREA_ICON), config.getBoolean("tray.enabled", false));
        misc.add(trayEnabled);
        final JCheckBox minimizeOnClose = new JCheckBox(LanguageUtil.getLocalText(LanguageConfigconst.SET_MINIMIZETO_TRAY_ON_CLOSE), config.getBoolean("tray.minimizeOnClose", true));
        misc.add(minimizeOnClose);
        misc.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_PATTERN_FOR_SHUFFLE_ALBUMS_MODE)));
        final JTextField patternField = new JTextField(config.getString("playbackOrder.albumFormat", "%album%"));
        misc.add(patternField);
        Box format = Box.createVerticalBox();
        format.setBorder(BorderFactory.createTitledBorder(LanguageUtil.getLocalText(LanguageConfigconst.SET_DISPLAY_FORMATTING)));
        format.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_WINDOW_TITLE)));
        format.add(Box.createVerticalStrut(2));
        final JTextField window = new JTextField(config.getString("format.window", ""));
        window.setCaretPosition(0);
        format.add(window);
        format.add(Box.createVerticalStrut(2));
        format.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_STATUS_BAR)));
        final JTextField status = new JTextField(config.getString("format.statusBar", ""));
        status.setCaretPosition(0);
        format.add(status);

        mainBox.add(Box.createVerticalStrut(20));
        mainBox.add(format);

        Box side = Box.createVerticalBox();
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBorder(BorderFactory.createTitledBorder(LanguageUtil.getLocalText(LanguageConfigconst.SET_SIDE_BAR)));
        sidePanel.add(side, BorderLayout.CENTER);

        final JCheckBox sidebar = new JCheckBox(LanguageUtil.getLocalText(LanguageConfigconst.SET_SHOW_SIDE_BAR), config.getBoolean("sidebar.enabled", true));
        side.add(sidebar);
        side.add(Box.createVerticalStrut(10));
        final JCheckBox lyrics = new JCheckBox(LanguageUtil.getLocalText(LanguageConfigconst.SET_SEARCH_LYRICS_ONLINE),
                config.getBoolean("lyrics.searchOnline", true));
        side.add(lyrics);
        side.add(Box.createVerticalStrut(5));
        Box aaBox = Box.createHorizontalBox();
        ButtonGroup bg = new ButtonGroup();
        boolean nowPlaying = config.getBoolean("albumart.nowPlayingOnly", false);
        final JRadioButton plTrack = new JRadioButton(LanguageUtil.getLocalText(LanguageConfigconst.SET_PLAYING_TRACK), nowPlaying);
        JRadioButton selTrack = new JRadioButton(LanguageUtil.getLocalText(LanguageConfigconst.SET_SELECTED_TRACK), !nowPlaying);
        bg.add(plTrack);
        bg.add(selTrack);
        aaBox.setAlignmentX(0);
        aaBox.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_SHOW_ALBUM_ART_FOR)));
        aaBox.add(Box.createHorizontalStrut(10));
        aaBox.add(plTrack);
        aaBox.add(Box.createHorizontalStrut(10));
        aaBox.add(selTrack);

        side.add(aaBox);
        side.add(Box.createVerticalStrut(5));

        side.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_ALBUM_ART_STUBS)));
        List<String> stubList = AlbumArtConfiguration.getStubs();
        StringBuilder sb = new StringBuilder();
        for (String s : stubList) {
            sb.append(s).append("\n");
        }
        final JTextArea stubs = new JTextArea(sb.toString(), 6, 1);
        stubs.setAlignmentX(0);
        side.add(stubs);

        mainBox.add(sidePanel);

        panel.add(mainBox, BorderLayout.NORTH);

        final JDialog comp = this;
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = laf.getSelectedIndex();
                if (index != -1) {
                    try {
                        String laf = lafs[index].getClassName();
                        UIManager.setLookAndFeel(laf);
                        SwingUtilities.updateComponentTreeUI(SwingUtilities.getRoot(owner));
                        SwingUtilities.updateComponentTreeUI(comp);
                        config.setString("gui.LAF", laf);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                config.setBoolean("tray.enabled", trayEnabled.isSelected());
                config.setBoolean("tray.minimizeOnClose", minimizeOnClose.isSelected());
                config.setString("format.window", window.getText());
                config.setString("format.statusBar", status.getText());
                config.setBoolean("sidebar.enabled", sidebar.isSelected());
                config.setBoolean("lyrics.searchOnline", lyrics.isSelected());
                config.setBoolean("albumart.nowPlayingOnly", plTrack.isSelected());
                config.setString("playbackOrder.albumFormat", patternField.getText());
                List<String> stubList = Arrays.asList(stubs.getText().split("\n"));
                AlbumArtConfiguration.setStubs(stubList);
            }
        });

        return panel;
    }

    private JComponent createColorsAndFontsPanel() {
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 30));
        panel.setName(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS));
        panel.setLayout(new BorderLayout());
        Box mainBox = Box.createVerticalBox();
        panel.add(mainBox, BorderLayout.NORTH);

        JPanel colors = new JPanel(new GridLayout(6, 2, 10, 10));
        colors.setBorder(BorderFactory.createTitledBorder(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_COLORS)));

        colors.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_TEXT)));
        final ColorChooser text = new ColorChooser(config.getColor("gui.color.text", null));
        colors.add(text);

        colors.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_BACKGROUND)));
        final ColorChooser background = new ColorChooser(config.getColor("gui.color.background", null));
        colors.add(background);

        colors.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_SELECTION)));
        final ColorChooser selection = new ColorChooser(config.getColor("gui.color.selection", null));
        colors.add(selection);

        colors.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_HIGHLIGHT)));
        final ColorChooser highlight = new ColorChooser(config.getColor("gui.color.highlight", null));
        colors.add(highlight);

        colors.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_TRAY_BACKGROUND1)));
        final ColorChooser trayBg1 = new ColorChooser(config.getColor("tray.bgColor1", null));
        colors.add(trayBg1);

        colors.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_TRAY_BACKGROUND2)));
        final ColorChooser trayBg2 = new ColorChooser(config.getColor("tray.bgColor2", null));
        colors.add(trayBg2);

        mainBox.add(colors);
        mainBox.add(Box.createVerticalStrut(20));

        JPanel fonts = new JPanel(new GridLayout(1, 2, 10, 10));
        fonts.setBorder(BorderFactory.createTitledBorder(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_FONTS)));

        fonts.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_COLORS_AND_FONTS_DEFAULT)));
        final FontChooser defaultFont = new FontChooser(config.getFont("gui.font.default", null));
        fonts.add(defaultFont);

//        fonts.add(new JLabel("Tabs"));
//        FontChooser tabsFont = new FontChooser(config.getFont("gui.font.tabs", null));
//        fonts.add(tabsFont);

        mainBox.add(fonts);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setColor("gui.color.text", text.getColor());
                config.setColor("gui.color.background", background.getColor());
                config.setColor("gui.color.selection", selection.getColor());
                config.setColor("gui.color.highlight", highlight.getColor());
                config.setFont("gui.font.default", defaultFont.getSelectedFont());
                config.setColor("tray.bgColor1", trayBg1.getColor());
                config.setColor("tray.bgColor2", trayBg2.getColor());
                SwingUtilities.updateComponentTreeUI(SwingUtilities.getRoot(owner));
            }
        });

        return panel;
    }

    private JComponent createSystemPanel() {
        final JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setName(LanguageUtil.getLocalText(LanguageConfigconst.SET_SYSTEM));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 30));

        Box mainBox = Box.createVerticalBox();
        JPanel misc = new JPanel(new GridLayout(2, 2, 10, 10));

        final AudioOutput output = app.getPlayer().getAudioOutput();

        Box mix = Box.createHorizontalBox();
        mix.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_SYSTEM_AUDIO_MIXER)));
        Vector<String> mixerVector = new Vector<String>();
        mixerVector.add(LanguageUtil.getLocalText(LanguageConfigconst.SET_SYSTEM_DETECT_AUTOMATICALLY));
        final Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        int selectedIndex = Arrays.asList(mixerInfo).indexOf(output.getMixer());
        for (Mixer.Info info : mixerInfo) {
            String s = info.getDescription() + ", " + info.getName();
            mixerVector.add(s);
        }
        final JComboBox mixers = new JComboBox(mixerVector);
        mixers.setSelectedIndex(selectedIndex + 1);
        mixers.setPrototypeDisplayValue(mixerVector.get(0));
        mix.add(mixers);
        mainBox.add(mix);
        mainBox.add(Box.createVerticalStrut(5));
        mainBox.add(misc);

        misc.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_SYSTEM_DEFAULT_ENCODING_FOR_TAGS)));
        Charset charset = AudioFileReader.getDefaultCharset();
        final JComboBox encoding = new JComboBox(Charset.availableCharsets().values().toArray());
        encoding.setSelectedItem(charset);
        misc.add(encoding);

        final JCheckBox oneInstance = new JCheckBox(LanguageUtil.getLocalText(LanguageConfigconst.SET_SYSTEM_ALLOW_ONLY_ONE_INSTANCE));
        oneInstance.setSelected(config.getBoolean("system.oneInstance", false));
        misc.add(oneInstance);

        panel.add(mainBox, BorderLayout.NORTH);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = mixers.getSelectedIndex();
                if (index > 0) {
                    Mixer.Info info = mixerInfo[index - 1];
                    output.setMixer(info);
                } else {
                    output.setMixer(null);
                }
                AudioFileReader.setDefaultCharset((Charset) encoding.getSelectedItem());
                config.setBoolean("system.oneInstance", oneInstance.isSelected());
            }
        });

        return panel;
    }

    private JComponent createNetworkPanel() {
        final JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setName(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 30));
        Box mainBox = Box.createVerticalBox();
        panel.add(mainBox, BorderLayout.NORTH);

        JPanel proxy = new JPanel(new GridLayout(5, 2, 0, 5));
        mainBox.add(proxy);
        proxy.setBorder(BorderFactory.createTitledBorder(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_HTTP_PROXY)));

        final JCheckBox proxyEnabled = new JCheckBox(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_USE_HTTP_PROXY));
        proxyEnabled.setSelected(config.getBoolean("proxy.enabled", false));
        proxy.add(proxyEnabled);
        proxy.add(new JLabel());

        proxy.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_HOST)));
        final JTextField host = new JTextField();
        host.setText(config.getString("proxy.host", null));
        proxy.add(host);

        proxy.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_PORT)));
        final JTextField port = new JTextField();
        port.setText(config.getString("proxy.port", null));
        proxy.add(port);

        proxy.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_USER_NAME)));
        final JTextField user = new JTextField();
        user.setText(config.getString("proxy.user", null));
        proxy.add(user);

        proxy.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_PASSWORD)));
        final JPasswordField password = new JPasswordField();
        password.setText(config.getString("proxy.password", null));
        proxy.add(password);

        final JPanel lastfm = new JPanel(new GridLayout(3, 2));
        lastfm.setBorder(BorderFactory.createTitledBorder(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_LAST_FM)));

        final JCheckBox lfmEnabled = new JCheckBox("Enable Last.fm Scrobbling");
        lastfm.add(lfmEnabled);
        lfmEnabled.setSelected(config.getBoolean("lastfm.enabled", false));
        lastfm.add(new JLabel());

        lastfm.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_USER_NAME)));
        final JTextField lfmUsername = new JTextField();
        lfmUsername.setText(config.getString("lastfm.user", null));
        lastfm.add(lfmUsername);

        lastfm.add(new JLabel(LanguageUtil.getLocalText(LanguageConfigconst.SET_NETWORK_PASSWORD)));
        final JPasswordField lfmPassword = new JPasswordField();
        lfmPassword.setText(config.getString("lastfm.password", null));
        lastfm.add(lfmPassword);

        mainBox.add(lastfm);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setBoolean("lastfm.enabled", lfmEnabled.isSelected());
                config.setString("lastfm.user", lfmUsername.getText());
                config.setString("lastfm.password", String.valueOf(lfmPassword.getPassword()));

                config.setBoolean("proxy.enabled", proxyEnabled.isSelected());
                config.setString("proxy.host", host.getText());
                config.setString("proxy.port", port.getText());
                config.setString("proxy.user", user.getText());
                config.setString("proxy.password", String.valueOf(password.getPassword()));

                if (proxyEnabled.isSelected()) {
                    System.setProperty("http.proxyHost", host.getText());
                    System.setProperty("http.proxyPort", port.getText());
                } else {
                    System.setProperty("http.proxyHost", "");
                    System.setProperty("http.proxyPort", "");
                }
            }
        });

        return panel;
    }

    private JComponent createLibraryPanel() {
        final JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setName(LanguageUtil.getLocalText(LanguageConfigconst.SET_LIBRARY));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 30));
        Box mainBox = Box.createVerticalBox();
        panel.add(mainBox, BorderLayout.NORTH);

        JPanel foldersPanel = new JPanel(new BorderLayout());
        foldersPanel.setBorder(BorderFactory.createTitledBorder(LanguageUtil.getLocalText(LanguageConfigconst.SET_LIBRARY_MUSIC_FOLDERS)));
        final ArrayList<String> model = new ArrayList<String>();
        model.addAll(LibraryConfiguration.getFolders(new ArrayList<String>()));
        final JList list = new JList(model.toArray());
        foldersPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        Container buttons = new JPanel(new GridLayout(0, 1));
        JButton add = new JButton(LanguageUtil.getLocalText(LanguageConfigconst.SET_LIBRARY_MUSIC_ADD));
        buttons.add(add);
        JButton remove = new JButton(LanguageUtil.getLocalText(LanguageConfigconst.SET_LIBRARY_MUSIC_REMOVE));
        buttons.add(remove);
        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(buttons);
        foldersPanel.add(p1, BorderLayout.LINE_END);
        mainBox.add(foldersPanel);

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreeFileChooser fileChooser = new TreeFileChooser(getRootPane(),LanguageUtil.getLocalText(LanguageConfigconst.SET_LIBRARY_MUSIC_SELECT_FOLDER), false);
                File[] files = fileChooser.showOpenDialog();
                for (File file : files) {
                    String path = file.getAbsolutePath();
                    if (!model.contains(path))
                        model.add(path);
                }
                list.setListData(model.toArray());
                list.repaint();
            }
        });

        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] values = list.getSelectedValues();
                if (values != null)
                    model.removeAll(Arrays.asList(values));
                list.setListData(model.toArray());
                list.repaint();
            }
        });

        final JCheckBox libraryView = new JCheckBox(LanguageUtil.getLocalText(LanguageConfigconst.SET_LIBRARY_MUSIC_ENABLE_LIBRARY_VIEW_PLAYLIST));
        libraryView.setSelected(config.getBoolean("library.libraryView", false));
        mainBox.add(libraryView);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibraryConfiguration.setFolders(model);
                config.setBoolean("library.libraryView", libraryView.isSelected());
            }
        });
        return panel;
    }

}
