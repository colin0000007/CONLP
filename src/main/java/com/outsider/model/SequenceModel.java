package com.outsider.model;

import java.util.List;

import com.outsider.model.hmm.SequenceNode;
/**
 * 序列模型接口
 * @author outsider
 *
 */
public interface SequenceModel extends Model<List<SequenceNode>, int[], int[]>{
}
