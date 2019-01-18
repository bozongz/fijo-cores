/**
 * 
 */
package com.fijo.cores.cache.redis;

import java.util.List;

import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessStep;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Lists;
import com.fijo.cores.app.model.ProcessHeader;
import com.fijo.cores.app.model.ProcessStep;

/**
 * @author zhangbo
 *
 */
public class KyroRedisSerializer<T> implements RedisSerializer<T> {
	public final Log log = LogFactory.getLog(KyroRedisSerializer.class);
	private Kryo kryo = new Kryo();

	@Override
	public byte[] serialize(Object t) throws SerializationException {
		log.debug("Kyro serialize:"+t.getClass().getName()+", value:"+t);
		byte[] buffer = new byte[2048];
		Output output = new Output(buffer);
		kryo.writeClassAndObject(output, t);
		return output.toBytes();
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		log.debug("Kyro deserialize:"+bytes);
		Input input = new Input(bytes);
		@SuppressWarnings("unchecked")
		T t = (T) kryo.readClassAndObject(input);
		return t;
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		ProcessHeader o = new ProcessHeader();
		o.setHeaderDesc("test");
		List<ProcessStep> steps = Lists.newArrayList();
		steps.add(new ProcessStep(3));
		steps.add(new ProcessStep(22));
		o.setSteps(steps);
		KyroRedisSerializer kyro = new KyroRedisSerializer();
		byte[] bytes1 = kyro.serialize(o);
		ProcessHeader o1 = (ProcessHeader) kyro.deserialize(bytes1);
		System.out.println(o1.getHeaderDesc());
		System.out.println(o1.getSteps().size());
	}
}
