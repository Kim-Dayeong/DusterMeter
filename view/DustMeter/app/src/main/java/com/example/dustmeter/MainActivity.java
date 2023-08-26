
    private Retrofit retrofit;
    private final String URL = "http://10.0.13.26:7700/"; //!!!!!!!!!!!

    public static Context context;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();





        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //위치 권한 확인

            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }


        //메뉴바
        init();
        NavigationViewHelper.enableNavigation(mContext, nav);



    }

    private NavigationView nav;
    private void init () {
        nav = findViewById(R.id.nav);

    }



    };





