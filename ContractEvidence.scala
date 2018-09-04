
/*
 * Copyright  2018 Blockchain Technology and Application Joint Lab, Fintech Research Center of ISCAS.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BA SIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import rep.sc.contract._

/**
 * 电子存证合约
 */

class ContractEvidence extends IContract{
case class Evidence(from:String, to:String)

  implicit val formats = DefaultFormats
  
  val file_hash =" "             //文件哈希（传值，sha256加密）
  val location_information=" "   //位置信息（ip定位，精确到市），后端调相关接口实现
  val time_service_center=" "    //时间（目前为NTP时间，后期会改进为授时中心时间），后端ntp协议时间实现
  val personnel_information=" "  //人员信息（post传值，用户名）
  val equipment_information=" "  //设备信息（post传值，mac地址）
  
  
    def init(ctx: ContractContext){      
      println(s"tid: $ctx.t.txid")
    }
    
    def set(ctx: ContractContext, data:Map[String,Int]):Object={
      println(s"set data:$data")
      for((k,v)<-data){
        ctx.api.setVal(k, v)
      }
      null
    }
    
    def put_proof(ctx: ContractContext, data:Map[String,String]):Object={     
      //先检查该hash是否已经存在,如果已存在,抛异常
      val k = data.get("file_hash").get
	    var pv0 = ctx.api.getVal(k)
	      if(pv0 != null)
		    throw new Exception("["+k+"]已存在，当前值["+pv0+"]");
	      ctx.api.setVal(k,data);
	      print("putProof:"+k+":"+data);
        var pv1 = ctx.api.getVal(k)
        print("putProof_Check:"+k+":"+pv1);
	    "put_proof ok"
    }
    
    def retrival(ctx: ContractContext, k:String):Object= {
       val v = ctx.api.getVal(k).toString()
       v
    }
    
    /**
     * 根据action,找到对应的method，并将传入的json字符串parse为method需要的传入参数
     */
    def onAction(ctx: ContractContext,action:String, sdata:String ):Object={
      //println(s"onAction---")
      //return "transfer ok"
      val json = parse(sdata)
      
      action match {
      //case "set" => 
      //  println(s"set") 
      //  set(ctx, json.extract[Map[String,Int]])
        case "put_proof" => 
          println(s"put_proof") 
          put_proof(ctx, json.extract[Map[String,String]])
        case "retrival"=>
          println(s"retrival")
          retrival(ctx, json.extract[String])
      }
    }
    
}
