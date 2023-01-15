// Express 기본 모듈 불러오기
var express = require('express')
    , http = require('http');

const mysql = require('mysql');


const fs = require('fs');

const bodyParser = require('body-parser') //html에서 서버에 데이터 요청

// 익스프레스 객체 생성
var app = express();

// 기본 포트를 app 객체에 속성으로 설정
app.set('port', process.env.PORT || 7700);



    //DB
const client = mysql.createConnection({
    host: "192.168.219.106",
    port:'3306',
    user: "root",
    password: "raspberry",
    database: "mysql"
});

// // 실내 미세먼지 값 가져오기 

app.get('/in',(req, res, next) => {

    client.query("select * from sensor order by date desc limit 1;", function(err, result, fields){
        if(err) throw err;
        else{
            

            var data =  {
                data: result[0],
            };
            

            res.end(JSON.stringify(data));
            //res.write(JSON.stringify(data));
            //next();
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




// app.use((req, res, next) => {

//     client.query("select * from sensor order by date desc limit 1;", function(err, result, fields){
//         if(err) throw err;
//         else{
            

//             var data =  {
//                 data: result[0],
//             };
            

            
//             res.write(JSON.stringify(data));
//             next();
//         }
//     });


// });



//실외 데이터 전송 
app.use((req, res, next) =>{


    var request = require('request');

    var url = 'http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty';
    var queryParams = '?' + encodeURIComponent('serviceKey') + '=aeyWMR9bHz6tch8FAPiOiuPoI%2BYvGImSqJSjQx7kB7rguqnMMP7WhAfCxH5xMk1kujpECK530srXKfaAofH26A%3D%3D'; /* Service Key*/
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

        //console.log(time);

        var page =  {
            time:time,
            pm10:pm10,
            pm25:pm25

        };

        //console.log(JSON.stringify(page).replace);

        res.end(JSON.stringify(page));

       // res.send(page);

        
        

    })



});


// Express 서버 시작
http.createServer(app).listen(app.get('port'), function(){
    console.log('익스프레스 서버를 시작했습니다 : ' + app.get('port'));
});


