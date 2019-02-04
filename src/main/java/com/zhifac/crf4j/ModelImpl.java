package com.zhifac.crf4j;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by zhifac on 2017/4/3.
 */
public class ModelImpl extends Model {
    private int nbest_;
    private int vlevel_;
    private DecoderFeatureIndex featureIndex_;

    public ModelImpl() {
        nbest_ = vlevel_ = 0;
        featureIndex_ = null;
    }

    public Tagger createTagger() {
        if (featureIndex_ == null) {
            return null;
        }
        TaggerImpl tagger = new TaggerImpl(TaggerImpl.Mode.TEST);
        tagger.open(featureIndex_, nbest_, vlevel_);
        return tagger;
    }

    public boolean open(String arg) {
        return open(arg.split(" ", -1));
    }

    public boolean open(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addRequiredOption("m", "model", true, "set FILE for model file");
        options.addOption("n", "nbest", true, "output n-best results");
        options.addOption("v", "verbose", true, "set INT for verbose level");
        options.addOption("c", "cost-factor", true, "set cost factor");

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch(ParseException e) {
            System.err.println("invalid arguments");
            return false;
        }
        String model = cmd.getOptionValue("m");
        int nbest = Integer.valueOf(cmd.getOptionValue("n", "0"));
        int vlevel = Integer.valueOf(cmd.getOptionValue("v", "0"));
        double costFactor = Double.valueOf(cmd.getOptionValue("c", "1.0"));
        return open(model, nbest, vlevel, costFactor);
    }
    /**
     * 
     * @author outsider 注释时间 2018.12.3
     * @param model 模型文件路径
     * @param nbest 每个观测位置的标注结果输出是按照概率排序的结果
     * @param vlevel 模型输出的详细程度
     * @param costFactor 代价因子，过拟合的衡量，默认为1，越大过越过拟合
     * @return
     */
    public boolean open(String model, int nbest, int vlevel, double costFactor) {
        featureIndex_ = new DecoderFeatureIndex();
        nbest_ = nbest;
        vlevel_ = vlevel;
        if (costFactor > 0) {
            featureIndex_.setCostFactor_(costFactor);
        }

        File f = new File(model);
        if (f.exists()) {
            try {
                FileInputStream stream = new FileInputStream(f);
                return featureIndex_.open(stream);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(model);
            if (stream != null) {
                return featureIndex_.open(stream);
            } else {
                System.err.println("Failed to find " + model + " in local path or classpath");
                return false;
            }
        }
    }

    public String getTemplate() {
        if (featureIndex_ != null) {
            return featureIndex_.getTemplate();
        } else {
            return null;
        }
    }

    public int getNbest_() {
        return nbest_;
    }

    public void setNbest_(int nbest_) {
        this.nbest_ = nbest_;
    }

    public int getVlevel_() {
        return vlevel_;
    }

    public void setVlevel_(int vlevel_) {
        this.vlevel_ = vlevel_;
    }

    public DecoderFeatureIndex getFeatureIndex_() {
        return featureIndex_;
    }

    public void setFeatureIndex_(DecoderFeatureIndex featureIndex_) {
        this.featureIndex_ = featureIndex_;
    }
}
