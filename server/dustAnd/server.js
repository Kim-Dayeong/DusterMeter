// Express 기본 모듈 불러오기
var express = require('express')
    , http = require('http');

const mysql = require('mysql');


const fs = require('fs');

const bodyParser = require('body-parser'); //html에서 서버에 데이터 요청
const { Router } = require('express');

const jwt = require('jsonwebtoken');
// 익스프레스 객체 생성
var app = express();

// 기본 포트를 app 객체에 속성으로 설정
app.set('port', process.env.PORT || 7700);

//geocoder 모듈, 옵션 설정
const nodeGeocoder = require('node-geocoder');
const { connect } = require('http2');
const Connection = require('mysql/lib/Connection');
const options = {
    provider: 'google',
    apiKey: 'Google API Key'
};

    //DB
const client = mysql.createConnection({
    host: "",
    port:'',
    user: "",  
    password: "",
    database: "mysql"
});

//실외 데이터 전송
//app.use(async(req, res, next) =>{
    app.use('/out',async(req, res, next) =>{

        try{
    
            const processedData = req.processedData;
        console.log("받은 데이터" + processedData);
    
        var request = require('request');
    
           
        var url = 'http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty';
        var queryParams = '?' + encodeURIComponent('serviceKey') + 'serverkeywritehere'; /* Service Key*/
        queryParams += '&' + encodeURIComponent('returnType') + '=' + encodeURIComponent('json'); /* */
        queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('100'); /* */
        queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); /* */
        queryParams += '&' + encodeURIComponent('sidoName') + '=' + encodeURIComponent('충남'); /* */
        queryParams += '&' + encodeURIComponent('ver') + '=' + encodeURIComponent('1.0'); /* */
    
    
        request({
            url: url + queryParams,
            method: 'GET'
    
        }, function (error, response, body) {
            // console.log('Status', response.statusCode);
            // console.log('Headers', JSON.stringify(response.headers));
            // console.log('Reponse received', body);
            const air = JSON.parse(body);
            time = air['response']['body']['items'][0]['dataTime'],
            pm10 =air['response']['body']['items'][0]['pm10Value'],
            pm25 = air['response']['body']['items'][0]['pm25Value']
    
            //미세먼지 기준
            var number;
            if (pm10 <=30){
                number = "좋음"
            }
            else if(31<pm10<80 ){
                number = "보통"
            }
            else if(81<pm10<150){
                number = "나쁨"
            }
            else if(pm10 <151){
                number = "매우 나쁨"
            }
            //console.log(time);
    
            var page =  {
                time:time,
                pm10:pm10,
                pm25:pm25,
                number:number
    
            };

    
            res.end(JSON.stringify(page));
    
    
        })
    
    
        }catch(err){
            console.error(err);
            res.status(500).send("Error writing to file ++++!!!!");
    
        }
    
    
    });
    


// 실내 미세먼지 값 가져오기

app.get('/in',(req, res, next) => {

    client.query("select * from sensor order by date desc limit 1;", function(err, result, fields){
        if(err) throw err;
        else{
           console.log("실내미세먼지값"+result[0]);

            var data =  {
                data: result[0],
            };
           

            res.end(JSON.stringify(data));
            //res.write(JSON.stringify(data));
            //next();
        }
    });


});

//유저 정보 
app.get('/user',(req, res, next) => {

    var name = req.query.name;

    client.query("Select blood,breath,asthma,young,old,skin,nose,eye from member where name = ?",[name],function(err, result, fields){
        if(err) throw err;
        else{
           //console.log(result[0]);
           var userdata = result[0];
           //console.log(userdata);

           var data = Object(JSON.parse(JSON.stringify(userdata)));
           //console.log(data);

           var arr = [];
          
           //데이터 파싱

           var var1 = data.blood;
           var var2 = data.breath;
           var var3 = data.asthma;
           var var4 = data.young;
           var var5 = data.old;
           var var6 = data.skin;
           var var7 = data.nose;
           var var8 = data.eye;

            // for(var i=1; i<=8; i++){
            //     let varName = 'var'+i;
            //     arr.push(eval(varName));
            // }

            for (var i = 1; i <= 8; i++) {
                let varName = 'var' + i;
                
                if (typeof eval(varName) !== 'undefined') {
                    arr.push(eval(varName));
                }
            }
            
          
         var userval = {
            data:arr
         }
           

            // var data =  {
            //     data: result[0],
            // };
           

            res.end(JSON.stringify(userval));
            //res.end((userval));
           
        }
    });


});

//그래프 페이지
app.get('/grp',(req,res,next)=> {

    client.query("SELECT DATE(date) AS date,MAX(pm20) AS max_daily_value FROM mysql.sensor WHERE date BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) AND NOW() GROUP BY DATE(date) ORDER BY max_daily_value DESC LIMIT 7;"
    ,function(err,result,fields){
        if(err) throw err;
        else{

            var value = {
                data : result
            }
            res.end(JSON.stringify(value));
           
        }

    })
   
})


//gps 값 받아오기
app.post('/gps', async(req, res, next) => {

    try{
         //console.log('who get in here post /users');
    var inputData;

    req.on('data', (data) => {
    inputData = JSON.parse(data);
    });

    req.on('end', () => {
    // var longitu = inputData.longitude;
    // var latitu = inputData.latitude;
    // console.log("longitude : "+inputData.longitude + " , latitude : "+inputData.latitude);
    var city = inputData.city;
    console.log("city :" + inputData.city);

    req.processedData = city;
    });

    res.write("OK!");
   
    next();


    } catch (err) {
        console.error("post 에러" + err);
        res.status(500).send("Error writing to file ++++");
    }

});

//로그인
app.use(express.json());
app.use(express.urlencoded( {extended : false } ));
app.post('/login',async(req, res, next)=>{
    console.log('로그인 중')
    console.log(req.body);
    var reqe= req.body;
    
    

    var userid = "";
    var pwd = "";

    userid = reqe.userid;
    pwd = reqe.pwd;
    console.log("파싱 테스트!!!"+userid);

   // const sql = 'select * from member where userid="${userid}"';
    client.query('select * from member where userid=?',[userid],function(err, data){
        //var test = daa.stringify; 

        const jsonString = JSON.stringify(data);
        const jsonArray = JSON.parse(jsonString);
      
        const name = jsonArray.map(row => row.name);
           

       console.log('data값!!!',data);
       
       console.log("이름!!!!!!"+name)

        //에러 
        if(data.length == 0){
            console.log('로그인 실패');
            res.status(400).json(
                {
                    "status" : "fail"
                }
            )
        }

        //에러 없으면 id 전송 
        else{
            console.log('로그인 성공');
            res.status(200).json(
                {
                    "userid":userid,
                    "name":name
                }
                )

        }
});

})


//회원가입
//app.use(express.json());
//app.use(express.urlencoded( {extended : false } ));
app.post('/signup',async(req, res, next)=>{
    console.log('회원가입 중')
    console.log(req.body);
    var reqe= req.body;

    // constructor(name,userid, pwd){
    //     this.name = name;
    //     this.first = userid;
    //     this.second = pwd;
    // }
    var name = "";
    var userid = "";
    var pwd = "";


    var blood;
   

    name = reqe.name;
    userid = reqe.userid;
    pwd = reqe.pwd;

    blood = reqe.blood;
    var breath = reqe.breath;
    var asthma = reqe.asthma;
    var young = reqe.young;
    var old = reqe.old;
    var skin = reqe.skin;
    var nose = reqe.nose;
    var eye = reqe.eye;
    

    console.log("파싱 테스트!!!"+blood);
    console.log("파싱 테스트!!!"+blood);


        client.query('select * from member where userid=?',[userid],(err, data) => {
            if (data.length == 0){
                console.log('회원가입 성공')
                client.query('insert into member(userid,pwd,name,blood,breath,asthma,young,old,skin,nose,eye) values (?,?,?,?,?,?,?,?,?,?,?)',
                [userid,pwd,name,blood,breath,asthma,young,old,skin,nose,eye]);
               
            }else {
                console.log('회원가입 실패'+err);
   
            }
});


   
})


// app.use('/use',req, res, next=>{
//     console.log("test");

// });



