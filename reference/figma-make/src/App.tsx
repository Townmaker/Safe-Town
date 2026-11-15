import { useCallback, useEffect, useState } from "react";
import ThreeVillage from "./ThreeVillage";
import ThreeVehicle, { type VehiclePart } from "./ThreeVehicle";

type IconName =
  | "arrow"
  | "calendar"
  | "car"
  | "chevron"
  | "clock"
  | "compass"
  | "expand"
  | "fuel"
  | "history"
  | "home"
  | "location"
  | "message"
  | "minus"
  | "music"
  | "phone"
  | "plus"
  | "road"
  | "shield"
  | "sparkles"
  | "settings"
  | "sun"
  | "user"
  | "users"
  | "volume";

function Icon({ name, size = 20 }: { name: IconName; size?: number }) {
  const paths: Record<IconName, React.ReactNode> = {
    arrow: <path d="m5 12 14-7-5.5 14-2.7-5.8L5 12Z" />,
    calendar: (
      <>
        <path d="M6 3v3M18 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v14H4V6a1 1 0 0 1 1-1Z" />
        <path d="M8 13h2M14 13h2M8 17h2" />
      </>
    ),
    car: (
      <>
        <path d="m5 16-1.5-1.8L5.7 8h12.6l2.2 6.2L19 16H5Z" />
        <path d="m7.3 8 1.3-3h6.8l1.3 3M7 16v3M17 16v3M7.5 12h.1M16.5 12h.1" />
      </>
    ),
    chevron: <path d="m9 6 6 6-6 6" />,
    clock: (
      <>
        <circle cx="12" cy="12" r="8.5" />
        <path d="M12 7v5l3.5 2" />
      </>
    ),
    compass: (
      <>
        <circle cx="12" cy="12" r="8.5" />
        <path d="m15.7 8.3-2.1 5.3-5.3 2.1 2.1-5.3 5.3-2.1Z" />
      </>
    ),
    expand: (
      <>
        <path d="M9 4H4v5M15 4h5v5M9 20H4v-5M15 20h5v-5" />
      </>
    ),
    fuel: (
      <>
        <path d="M5 21V4h10v17M4 21h12M7 8h6M15 8h2l2 3v7a1.5 1.5 0 0 0 3 0v-6l-2-2" />
      </>
    ),
    history: (
      <>
        <path d="M4 5v5h5" />
        <path d="M5.7 9.2A7.5 7.5 0 1 1 5 15" />
        <path d="M12 8v4l3 2" />
      </>
    ),
    home: (
      <>
        <path d="m3 11 9-7 9 7" />
        <path d="M5 10v10h14V10M9 20v-6h6v6" />
      </>
    ),
    location: (
      <>
        <path d="M19 10c0 5-7 11-7 11S5 15 5 10a7 7 0 1 1 14 0Z" />
        <circle cx="12" cy="10" r="2.2" />
      </>
    ),
    message: (
      <>
        <path d="M4 5h16v11H9l-5 4V5Z" />
        <path d="M8 10h.1M12 10h.1M16 10h.1" />
      </>
    ),
    minus: <path d="M5 12h14" />,
    music: (
      <>
        <path d="M9 18V5l10-2v13" />
        <circle cx="6" cy="18" r="3" />
        <circle cx="16" cy="16" r="3" />
      </>
    ),
    phone: <path d="M7 3 4 5c0 8 7 15 15 15l2-3-5-3-2 2c-3-1-5-3-6-6l2-2-3-5Z" />,
    plus: <path d="M12 5v14M5 12h14" />,
    road: (
      <>
        <path d="m8 3-3 18M16 3l3 18M12 4v3M12 10v4M12 17v3" />
      </>
    ),
    shield: (
      <>
        <path d="M12 3 5 6v5c0 4.5 2.8 8 7 10 4.2-2 7-5.5 7-10V6l-7-3Z" />
        <path d="m9 12 2 2 4-4" />
      </>
    ),
    sparkles: (
      <>
        <path d="m12 3 1.3 3.7L17 8l-3.7 1.3L12 13l-1.3-3.7L7 8l3.7-1.3L12 3ZM19 14l.8 2.2L22 17l-2.2.8L19 20l-.8-2.2L16 17l2.2-.8L19 14ZM5 13l.8 2.2L8 16l-2.2.8L5 19l-.8-2.2L2 16l2.2-.8L5 13Z" />
      </>
    ),
    settings: (
      <>
        <circle cx="12" cy="12" r="3" />
        <path
          d="M19 13.5v-3l-2-.6a7 7 0 0 0-.7-1.7l1-1.8-2.1-2.1-1.8 1a7 7 0 0 0-1.8-.7L11 2.5H8l-.6 2.1a7 7 0 0 0-1.7.7l-1.8-1-2.1 2.1 1 1.8A7 7 0 0 0 2.1 10L0 10.5v3l2.1.6a7 7 0 0 0 .7 1.7l-1 1.8 2.1 2.1 1.8-1a7 7 0 0 0 1.7.7l.6 2.1h3l.6-2.1a7 7 0 0 0 1.8-.7l1.8 1 2.1-2.1-1-1.8a7 7 0 0 0 .7-1.7Z"
          transform="translate(1.5 0) scale(.88)"
        />
      </>
    ),
    sun: (
      <>
        <circle cx="12" cy="12" r="3.5" />
        <path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4" />
      </>
    ),
    user: (
      <>
        <circle cx="12" cy="8" r="4" />
        <path d="M4.5 21a7.5 7.5 0 0 1 15 0" />
      </>
    ),
    users: (
      <>
        <circle cx="9" cy="8" r="3" />
        <circle cx="17" cy="10" r="2.5" />
        <path d="M3 20a6 6 0 0 1 12 0M14 16a5 5 0 0 1 7 4" />
      </>
    ),
    volume: (
      <>
        <path d="M4 10v4h4l5 4V6l-5 4H4Z" />
        <path d="M17 9a4 4 0 0 1 0 6M19 6a8 8 0 0 1 0 12" />
      </>
    ),
  };

  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {paths[name]}
    </svg>
  );
}

function VillageArtwork() {
  return (
    <svg
      className="village-art"
      viewBox="0 0 960 600"
      role="img"
      aria-label="강과 도로가 흐르는 친환경 미래 마을"
    >
      <defs>
        <linearGradient id="land" x1="0" y1="0" x2="1" y2="1">
          <stop stopColor="#8bcf91" />
          <stop offset="1" stopColor="#3b9168" />
        </linearGradient>
        <linearGradient id="road" x1="0" y1="0" x2="1" y2="1">
          <stop stopColor="#657b83" />
          <stop offset="1" stopColor="#364c58" />
        </linearGradient>
        <linearGradient id="water" x1="0" y1="0" x2="1" y2="1">
          <stop stopColor="#65d1dd" />
          <stop offset="1" stopColor="#1685a4" />
        </linearGradient>
        <linearGradient id="tower" x1="0" y1="0" x2="1" y2="1">
          <stop stopColor="#ddf8ff" />
          <stop offset=".6" stopColor="#70bbd3" />
          <stop offset="1" stopColor="#326a85" />
        </linearGradient>
        <filter id="shadow">
          <feDropShadow dx="0" dy="12" stdDeviation="10" floodColor="#07141c" floodOpacity=".28" />
        </filter>
        <filter id="glow">
          <feGaussianBlur stdDeviation="8" result="b" />
          <feMerge>
            <feMergeNode in="b" />
            <feMergeNode in="SourceGraphic" />
          </feMerge>
        </filter>
      </defs>
      <path
        d="M87 227 350 79 875 317 606 545 71 371Z"
        fill="url(#land)"
        stroke="#a7e6b2"
        strokeWidth="5"
        filter="url(#shadow)"
      />
      <path
        d="M75 317c152-3 205-65 303-77 131-16 217 93 347 75 53-8 96-31 132-53l19 55c-43 28-90 55-146 64-148 23-243-88-373-68-102 15-162 79-280 72Z"
        fill="url(#water)"
        opacity=".96"
      />
      <path
        d="M95 250 583 490M206 173l484 234M398 99 796 333M191 390 489 190M373 478 665 249M588 526 835 323"
        stroke="url(#road)"
        strokeWidth="38"
        strokeLinecap="round"
      />
      <path
        d="M95 250 583 490M206 173l484 234M398 99 796 333M191 390 489 190M373 478 665 249M588 526 835 323"
        stroke="#d9eef0"
        strokeWidth="2.5"
        strokeDasharray="13 13"
        opacity=".75"
      />
      <path d="m349 255 79-43 91 43-80 47Z" fill="#d1e8e6" stroke="#9dc7c5" strokeWidth="4" />
      <path d="M290 318 475 213M487 379l136-109" stroke="#d7e3dd" strokeWidth="20" />
      <path d="M290 318 475 213M487 379l136-109" stroke="#647982" strokeWidth="13" />
      <g filter="url(#shadow)">
        <path d="m417 224 47-26 51 27-48 28Z" fill="#bde7f3" />
        <path d="m417 224 50 29v91l-50-29Z" fill="#4a8aa6" />
        <path d="m467 253 48-28v90l-48 29Z" fill="url(#tower)" />
        <path d="m442 208 22-77 25 79-24 14Z" fill="#d7f8ff" />
        <path
          d="M456 177h18M451 195h29M478 261v58M490 254v58M502 246v58"
          stroke="#e8fbff"
          strokeWidth="5"
          opacity=".72"
        />
      </g>
      <g className="village-lights" fill="#8af9ff" filter="url(#glow)">
        <circle cx="464" cy="127" r="5" />
        <circle cx="381" cy="383" r="4" />
        <circle cx="632" cy="349" r="4" />
        <circle cx="731" cy="300" r="4" />
      </g>
      {[
        [182, 216, "#e89055"],
        [245, 285, "#f5c658"],
        [308, 174, "#e97a6c"],
        [613, 220, "#f3c964"],
        [690, 276, "#e98463"],
        [542, 410, "#d98062"],
        [701, 393, "#edb95e"],
        [261, 390, "#f1c85c"],
      ].map(([x, y, color], i) => (
        <g key={i} transform={`translate(${x} ${y})`} filter="url(#shadow)">
          <path d="m0 7 30-17 35 19-31 18Z" fill={color as string} />
          <path d="m0 7 34 20v38L0 45Z" fill="#e8f0df" />
          <path d="m34 27 31-18v38L34 65Z" fill="#b9d6cc" />
          <path d="M42 31h12v13H42Z" fill="#4e8e9b" />
          <path d="M12 21h10v12H12Z" fill="#67a4af" />
        </g>
      ))}
      {[
        [145, 305],
        [220, 145],
        [334, 420],
        [574, 170],
        [640, 450],
        [770, 337],
        [790, 265],
        [516, 150],
        [153, 356],
        [728, 435],
      ].map(([x, y], i) => (
        <g key={i} transform={`translate(${x} ${y})`} filter="url(#shadow)">
          <path d="M0 15v40" stroke="#7c5b42" strokeWidth="7" />
          <circle cx="0" cy="6" r="22" fill={i % 2 ? "#48a76d" : "#58b779"} />
          <circle cx="-12" cy="13" r="14" fill="#79ca80" />
        </g>
      ))}
      <g transform="translate(570 296)" filter="url(#shadow)">
        <ellipse cx="0" cy="34" rx="43" ry="24" fill="#eef8f1" />
        <path d="M-9 29h18v-76H-9Z" fill="#e8f1ef" />
        <circle cy="-50" r="7" fill="#e8f1ef" />
        <path
          d="M0-50 3-107M0-50l54 20M0-50l-48 31"
          stroke="#f4fbfa"
          strokeWidth="8"
          strokeLinecap="round"
        />
      </g>
      <g transform="translate(378 116)" filter="url(#shadow)">
        <ellipse cx="0" cy="18" rx="34" ry="18" fill="#dfeee8" />
        <path d="M-23 14 0-25l23 39Z" fill="#b6d1c8" />
        <path d="M-15 7 0-17 15 7Z" fill="#70a3a3" />
      </g>
      <g transform="translate(415 375)">
        <ellipse rx="23" ry="13" fill="#1c3b4b" opacity=".35" />
        <path d="M-16-4h29l8 8-4 10h-35l-4-9Z" fill="#71d0f4" stroke="#e3fbff" strokeWidth="2" />
        <circle cx="-11" cy="13" r="5" fill="#142c39" />
        <circle cx="12" cy="13" r="5" fill="#142c39" />
      </g>
    </svg>
  );
}

const villageStages = [
  ["움집 마을", "첫 정착지"],
  ["초가집 마을", "농경의 시작"],
  ["기와집 마을", "전통 고을"],
  ["벽돌집 마을", "근대 도시의 시작"],
  ["단독주택 마을", "푸른 정원 도시"],
  ["연립주택 마을", "이웃이 모인 생활권"],
  ["아파트 도시", "철도와 대단지"],
  ["초고층 주상복합", "빛나는 수직 도시"],
  ["스마트 친환경 타워", "자율주행과 공중 정원"],
  ["공중부양 주거단지", "하늘길이 열린 도시"],
  ["우주 돔 거주기지", "행성 위 새로운 생활"],
  ["은하 우주정거장", "여러 행성을 잇는 우주항"],
];

const friends = [
  { name: "민준", stage: 8, status: "스마트 친환경 타워 · 접속 중" },
  { name: "서윤", stage: 6, status: "아파트 도시 · 12분 전" },
  { name: "도현", stage: 4, status: "초가 마을 · 1시간 전" },
  { name: "하린", stage: 11, status: "은하 우주정거장 · 어제" },
];

function VillageScreen({
  onStart,
  isDriving,
  onResume,
  onNavigate,
  showStartupCheck,
  onStartupCheckShown,
  friendVisit,
  onReturnOwnVillage,
}: {
  onStart: () => void;
  isDriving: boolean;
  onResume: () => void;
  onNavigate: (page: Page) => void;
  showStartupCheck: boolean;
  onStartupCheckShown: () => void;
  friendVisit: (typeof friends)[number] | null;
  onReturnOwnVillage: () => void;
}) {
  const [chromeVisible, setChromeVisible] = useState(true);
  const [stage, setStage] = useState(friendVisit?.stage ?? 0);
  const [modal, setModal] = useState<"friends" | "collection" | null>(null);
  const [owner, setOwner] = useState(friendVisit ? `${friendVisit.name}의 마을` : "나의 마을");
  const [showVehicleCheck, setShowVehicleCheck] = useState(false);
  const toggleChrome = useCallback(() => setChromeVisible((value) => !value), []);
  const stageInfo = villageStages[stage];
  const era = stage <= 1 ? 0 : stage <= 5 ? 1 : stage <= 8 ? 2 : 3;

  useEffect(() => {
    if (!showStartupCheck) return;
    const timer = window.setTimeout(() => setShowVehicleCheck(true), 1400);
    const markTimer = window.setTimeout(onStartupCheckShown, 1400);
    return () => {
      window.clearTimeout(timer);
      window.clearTimeout(markTimer);
    };
  }, [showStartupCheck, onStartupCheckShown]);

  return (
    <main className={`village-screen era-${era} screen-enter`}>
      <div className="aurora aurora-one" />
      <div className="aurora aurora-two" />
      <section className="village-stage">
        <ThreeVillage era={stage} onTap={toggleChrome} />
      </section>

      <div className={`home-chrome ${chromeVisible ? "" : "is-hidden"}`}>
        <header className="home-header">
          <div className="brand">
            <span className="brand-mark">
              <Icon name="road" size={22} />
            </span>
            <div>
              <strong>Safe Town</strong>
            </div>
          </div>
          <div className="home-date">
            <Icon name="calendar" size={20} />
            <span>6월 12일 수요일</span>
            <b>10:24</b>
          </div>
        </header>

        <aside className="level-card glass-card">
          <div className="home-grade-top">
            <div>
              <small>{owner === "나의 마을" ? "나의" : owner} 안전운전 등급</small>
              <h1>
                <b>Lv.1</b> 입니다
              </h1>
              <p>
                누적 안전운전거리{" "}
                <strong>{stage === 0 ? "2,277km" : `${(stage + 1) * 1240}km`}</strong>
              </p>
            </div>
            <span className="grade-emblem">
              <b>LV.1</b>
            </span>
          </div>
          <div className="grade-progress-label">
            <span>다음 레벨까지</span>
            <b>723km 남음</b>
          </div>
          <div className="grade-progress">
            <i style={{ width: "76%" }} />
          </div>
          <div className="grade-progress-scale">
            <span>0km</span>
            <span>3,000km</span>
          </div>
          <div className="grade-card-footer">
            <span>
              <Icon name="home" size={17} />
              <b>{stageInfo[0]}</b>
            </span>
            <em>오늘 +180 XP</em>
          </div>
        </aside>

        <div className="village-side-actions">
          <button className="glass-card" onClick={() => setModal("friends")}>
            <Icon name="users" size={24} />
            <span>
              <b>친구 마을</b>
              <small>4명 접속 중</small>
            </span>
          </button>
          <button className="glass-card" onClick={() => setModal("collection")}>
            <Icon name="shield" size={24} />
            <span>
              <b>컬렉션</b>
              <small>12 / 48 배지</small>
            </span>
          </button>
        </div>

        <div className="view-guide glass-card">
          <span>
            <i className="gesture-icon">↔</i>
            <b>드래그</b> 둘러보기
          </span>
          <span>
            <i className="gesture-icon">⌁</i>
            <b>핀치</b> 확대 · 축소
          </span>
          <span>
            <i className="gesture-icon">○</i>
            <b>터치</b> 화면만 보기
          </span>
        </div>

        {!isDriving && (
          <button
            className="start-button"
            onClick={(event) => {
              event.stopPropagation();
              onStart();
            }}
          >
            <span className="start-icon">
              <Icon name="arrow" size={27} />
            </span>
            <span>
              <small>오늘도 안전하게</small>
              <b>주행 시작</b>
            </span>
            <Icon name="chevron" size={22} />
          </button>
        )}
      </div>
      {isDriving && (
        <button className="driving-float" onClick={onResume}>
          <span className="live-dot" />
          <span>
            <small>현재 주행 중</small>
            <b>내비게이션으로 돌아가기</b>
          </span>
          <Icon name="chevron" size={19} />
        </button>
      )}
      <div className="home-app-nav">
        <AppNav
          onNavigate={onNavigate}
          active="village"
          isDriving={isDriving}
          onStartDrive={onStart}
        />
      </div>
      {modal && (
        <div className="modal-backdrop" onClick={() => setModal(null)}>
          <section className="village-modal" onClick={(event) => event.stopPropagation()}>
            <header>
              <div>
                <small>{modal === "friends" ? "함께 성장하는 이웃" : "지역 방문 기록"}</small>
                <h2>{modal === "friends" ? "친구의 마을" : "나의 배지 컬렉션"}</h2>
              </div>
              <button onClick={() => setModal(null)}>×</button>
            </header>
            {modal === "friends" ? (
              <div className="friend-list">
                {friends.map((friend) => (
                  <button
                    key={friend.name}
                    onClick={() => {
                      setStage(friend.stage);
                      setOwner(`${friend.name}의 마을`);
                      setModal(null);
                    }}
                  >
                    <span className="friend-avatar">{friend.name[0]}</span>
                    <span>
                      <b>{friend.name}</b>
                      <small>{friend.status}</small>
                    </span>
                    <Icon name="chevron" size={18} />
                  </button>
                ))}
                {owner !== "나의 마을" && (
                  <button
                    className="my-village-return"
                    onClick={() => {
                      setStage(0);
                      setOwner("나의 마을");
                      onReturnOwnVillage();
                      setModal(null);
                    }}
                  >
                    나의 마을로 돌아가기
                  </button>
                )}
              </div>
            ) : (
              <div className="badge-grid">
                {[
                  "서울 한강",
                  "부산 해운대",
                  "경주 첨성대",
                  "제주 오름",
                  "강릉 바다",
                  "전주 한옥",
                  "인천 대교",
                  "속초 설악",
                  "대전 엑스포",
                  "여수 밤바다",
                  "광주 무등산",
                  "수원 화성",
                  "통영 항구",
                  "춘천 호수",
                  "울산 대왕암",
                  "포항 호미곶",
                  "군산 근대항",
                  "담양 대나무",
                  "안동 하회마을",
                  "태백 별빛",
                  "평창 대관령",
                  "남해 다랭이",
                  "목포 해상로",
                  "세종 호수공원",
                ].map((badge, index) => (
                  <div className={index > 4 ? "locked" : ""} key={badge}>
                    <span>
                      <Icon name={index > 4 ? "shield" : "sparkles"} size={22} />
                    </span>
                    <b>{badge}</b>
                    <small>{index > 4 ? "미획득" : "방문 완료"}</small>
                  </div>
                ))}
              </div>
            )}
          </section>
        </div>
      )}
      {showVehicleCheck && (
        <div className="vehicle-check-pop">
          <header>
            <span>
              <Icon name="car" size={22} />
            </span>
            <div>
              <small>PLEOS VEHICLE CHECK</small>
              <h2>출발 전 차량 상태</h2>
            </div>
            <button onClick={() => setShowVehicleCheck(false)}>×</button>
          </header>
          <div className="vehicle-check-grid">
            {[
              ["EV 배터리", "72%", true],
              ["타이어 공기압", "정상", true],
              ["안전벨트", "체결", true],
              ["도어 잠금", "완료", true],
              ["브레이크액", "정상", true],
              ["워셔액", "보충 필요", false],
              ["엔진오일 경고등", "소등", true],
              ["미션오일 경고등", "점검 필요", false],
            ].map(([label, value, good]) => (
              <div className={good ? "good" : "warning"} key={String(label)}>
                <span>{good ? "✓" : "!"}</span>
                <small>{label}</small>
                <b>{value}</b>
              </div>
            ))}
          </div>
          <div className="check-reward">
            <Icon name="sparkles" size={18} />
            <span>
              <b>안전 조건 6개 충족</b>
              <small>차량 관리 보너스 +15 XP</small>
            </span>
          </div>
          <p className="maintenance-warning">
            워셔액 보충 및 미션오일 점검이 필요합니다. 다음 주행 전 정비해 주세요.
          </p>
        </div>
      )}
    </main>
  );
}

const initialRules = [
  {
    id: 1,
    distance: "120m",
    title: "어린이 보호구역",
    detail: "제한속도 30km/h",
    kind: "30",
    color: "#ffb648",
  },
  {
    id: 2,
    distance: "420m",
    title: "신호 과속 단속",
    detail: "신호 변경에 주의하세요",
    kind: "CAM",
    color: "#ff6676",
  },
  {
    id: 3,
    distance: "680m",
    title: "우측 차로 합류",
    detail: "합류 차량에 양보하세요",
    kind: "↘",
    color: "#63d8cc",
  },
];

type Page = "village" | "driving" | "history" | "profile" | "settings" | "result";

const navItems: { page: Page; label: string; icon: IconName }[] = [
  { page: "village", label: "홈", icon: "home" },
  { page: "history", label: "주행히스토리", icon: "history" },
  { page: "driving", label: "주행", icon: "road" },
  { page: "profile", label: "마이페이지", icon: "user" },
  { page: "settings", label: "설정", icon: "settings" },
];

const quickActionOptions: { id: string; label: string; icon: IconName }[] = [
  { id: "phone", label: "전화", icon: "phone" },
  { id: "message", label: "메시지", icon: "message" },
  { id: "music", label: "음악", icon: "music" },
  { id: "ai", label: "AI에게 말하기", icon: "sparkles" },
  { id: "home", label: "집으로", icon: "home" },
  { id: "history", label: "최근 목적지", icon: "history" },
];

function AppNav({
  onNavigate,
  active = "driving",
  isDriving = true,
  onStartDrive,
}: {
  onNavigate: (page: Page) => void;
  active?: Page;
  isDriving?: boolean;
  onStartDrive?: () => void;
}) {
  return (
    <nav className="app-nav" aria-label="주요 메뉴">
      {navItems.map((item) => {
        const driving = item.page === "driving";
        return (
          <button
            key={item.page}
            className={`${driving && isDriving ? "driving-now" : ""} ${active === item.page ? "nav-active" : ""}`}
            onClick={() => {
              if (active === item.page) return;
              if (driving && !isDriving) onStartDrive?.();
              else onNavigate(item.page);
            }}
            aria-current={active === item.page ? "page" : undefined}
          >
            <span>
              {driving && isDriving && <i className="nav-live-dot" />}
              <Icon name={item.icon} size={20} />
            </span>
            <b>{item.label}</b>
            {driving && isDriving && <small>주행 중</small>}
          </button>
        );
      })}
    </nav>
  );
}

function DriveActionPanel({ feature, onClose }: { feature: string; onClose: () => void }) {
  const [activeCall, setActiveCall] = useState<string | null>(null);
  const [playing, setPlaying] = useState(false);
  const [messageSent, setMessageSent] = useState(false);
  const [listening, setListening] = useState(false);
  const [aiReply, setAiReply] = useState("무엇을 도와드릴까요?");
  const featureInfo =
    quickActionOptions.find((item) => item.id === feature) ?? quickActionOptions[0];

  const renderBody = () => {
    if (feature === "phone") {
      if (activeCall)
        return (
          <div className="active-call">
            <span className="call-avatar">{activeCall[0]}</span>
            <small>통화 중 · 차량 스피커</small>
            <h2>{activeCall}</h2>
            <b>00:18</b>
            <div>
              <button>음소거</button>
              <button>키패드</button>
              <button>오디오</button>
            </div>
            <button className="hangup" onClick={() => setActiveCall(null)}>
              통화 종료
            </button>
          </div>
        );
      return (
        <div className="phone-panel">
          <div className="panel-search">
            <Icon name="user" size={17} />
            <input placeholder="연락처 검색" />
          </div>
          <h3>즐겨찾기</h3>
          <div className="favorite-contacts">
            {["집", "김민준", "박서윤", "회사"].map((name) => (
              <button key={name} onClick={() => setActiveCall(name)}>
                <span>{name[0]}</span>
                <b>{name}</b>
              </button>
            ))}
          </div>
          <h3>최근 통화</h3>
          <div className="action-list">
            {[
              ["이도현", "수신 · 오늘 09:42"],
              ["정하린", "발신 · 어제 18:11"],
              ["보험 긴급출동", "수신 · 6월 10일"],
            ].map(([name, meta]) => (
              <button key={name} onClick={() => setActiveCall(name)}>
                <span className="list-avatar">{name[0]}</span>
                <span>
                  <b>{name}</b>
                  <small>{meta}</small>
                </span>
                <Icon name="phone" size={18} />
              </button>
            ))}
          </div>
        </div>
      );
    }
    if (feature === "message") {
      return (
        <div className="message-panel">
          <div className="conversation-head">
            <span className="list-avatar">김</span>
            <span>
              <b>김민준</b>
              <small>안전 운전 중</small>
            </span>
          </div>
          <div className="message-bubbles">
            <p>도착하면 연락 줘!</p>
            {messageSent && <p className="mine">지금 운전 중이에요. 도착 후 연락드릴게요.</p>}
          </div>
          <h3>운전 중 빠른 답장</h3>
          <div className="quick-replies">
            {["지금 운전 중이에요.", "약 20분 후 도착해요.", "도착 후 연락드릴게요."].map(
              (text) => (
                <button key={text} onClick={() => setMessageSent(true)}>
                  {text}
                </button>
              ),
            )}
          </div>
          <button className="voice-message">
            <Icon name="sparkles" size={18} /> 음성으로 메시지 작성
          </button>
        </div>
      );
    }
    if (feature === "music") {
      return (
        <div className="music-panel">
          <div className="album-art">
            <Icon name="music" size={48} />
            <i />
          </div>
          <small>DRIVE MIX · 재생 중</small>
          <h2>Blue Morning Road</h2>
          <p>Chacha City Sounds</p>
          <div className="music-progress">
            <i style={{ width: "42%" }} />
          </div>
          <div className="music-time">
            <span>1:48</span>
            <span>4:12</span>
          </div>
          <div className="music-controls">
            <button>‹‹</button>
            <button className="play" onClick={() => setPlaying((value) => !value)}>
              {playing ? "Ⅱ" : "▶"}
            </button>
            <button>››</button>
          </div>
          <h3>다음 재생</h3>
          <div className="action-list compact">
            {["Weekend Highway", "City Lights", "Green Village"].map((track) => (
              <button key={track}>
                <Icon name="music" size={17} />
                <span>
                  <b>{track}</b>
                  <small>Drive Collection</small>
                </span>
                <span>3:42</span>
              </button>
            ))}
          </div>
        </div>
      );
    }
    if (feature === "ai") {
      return (
        <div className="ai-panel">
          <div className={`ai-orb ${listening ? "listening" : ""}`}>
            <Icon name="sparkles" size={34} />
          </div>
          <small>{listening ? "듣고 있어요…" : "차차 AI"}</small>
          <h2>{aiReply}</h2>
          <button
            className="ai-mic"
            onClick={() => {
              setListening(true);
              window.setTimeout(() => {
                setListening(false);
                setAiReply("가장 빠른 경로로 안내를 시작할게요.");
              }, 1200);
            }}
          >
            <Icon name="sparkles" size={20} /> {listening ? "음성 인식 중" : "말하기"}
          </button>
          <div className="ai-suggestions">
            {[
              "집으로 안내해줘",
              "실내 온도 22도로",
              "오늘 안전점수 알려줘",
              "잔잔한 음악 틀어줘",
            ].map((text) => (
              <button key={text} onClick={() => setAiReply(`${text} 요청을 실행했어요.`)}>
                {text}
              </button>
            ))}
          </div>
        </div>
      );
    }
    return (
      <div className="generic-action">
        <Icon name={featureInfo.icon} size={38} />
        <h2>{featureInfo.label}</h2>
        <p>최근 목적지와 즐겨찾기를 확인할 수 있어요.</p>
        <button onClick={onClose}>확인</button>
      </div>
    );
  };

  return (
    <div className="feature-backdrop" onClick={onClose}>
      <section
        className={`drive-action-panel panel-${feature}`}
        onClick={(event) => event.stopPropagation()}
      >
        <header>
          <div>
            <span>
              <Icon name={featureInfo.icon} size={21} />
            </span>
            <div>
              <small>주행 중 빠른 실행</small>
              <h2>{featureInfo.label}</h2>
            </div>
          </div>
          <button onClick={onClose}>×</button>
        </header>
        <div className="drive-action-body">{renderBody()}</div>
      </section>
    </div>
  );
}

function DrivingScreen({
  elapsed,
  onEnd,
  onNavigate,
  quickActions,
}: {
  elapsed: number;
  onEnd: () => void;
  onNavigate: (page: Page) => void;
  quickActions: string[];
}) {
  const [mapZoom, setMapZoom] = useState(1);
  const [rules, setRules] = useState(initialRules);
  const [searchOpen, setSearchOpen] = useState(false);
  const [destination, setDestination] = useState("");
  const [routeState, setRouteState] = useState<"idle" | "searching" | "active">("idle");
  const [ruleVerdict, setRuleVerdict] = useState<{
    id: number;
    type: "good" | "close" | "bad";
  } | null>(null);
  const [actionPanel, setActionPanel] = useState<string | null>(null);

  const driveTime = `${String(Math.floor(elapsed / 60)).padStart(2, "0")}:${String(elapsed % 60).padStart(2, "0")}`;

  useEffect(() => {
    if (!rules.length || ruleVerdict) return;
    const timer = window.setTimeout(() => {
      const current = rules[0];
      const verdicts: ("good" | "close" | "bad")[] = ["good", "close", "good"];
      setRuleVerdict({ id: current.id, type: verdicts[(current.id - 1) % verdicts.length] });
    }, 4200);
    return () => window.clearTimeout(timer);
  }, [rules, ruleVerdict]);

  useEffect(() => {
    if (!ruleVerdict) return;
    const timer = window.setTimeout(() => {
      setRules((items) => items.filter((item) => item.id !== ruleVerdict.id));
      setRuleVerdict(null);
    }, 1500);
    return () => window.clearTimeout(timer);
  }, [ruleVerdict]);

  const requestFastestRoute = (place: string) => {
    if (!place.trim()) return;
    setDestination(place);
    setRouteState("searching");
    setSearchOpen(false);
    window.setTimeout(() => setRouteState("active"), 900);
  };

  return (
    <main className="drive-screen screen-enter">
      <header className="drive-header">
        <div className="drive-brand">
          <span className="brand-mark">
            <Icon name="road" size={20} />
          </span>
          <span className="drive-brand-copy">
            <strong>Safe Town</strong>
            <small>안전운전으로 성장하는 도시</small>
          </span>
        </div>
        <div className="status-strip">
          <div className="status-item">
            <Icon name="calendar" size={16} />
            <span>
              <small>2025. 06. 12</small>
              <b>수요일</b>
            </span>
          </div>
          <div className="status-item time">
            <Icon name="clock" size={16} />
            <span>
              <small>현재 시각</small>
              <b>10:24</b>
            </span>
          </div>
          <div className="status-item speed">
            <Icon name="sparkles" size={19} />
            <span>
              <small>이번 주행 경험치</small>
              <b>
                +{Math.floor(elapsed / 5)} <em>XP</em>
              </b>
            </span>
          </div>
          <div className="status-item">
            <Icon name="fuel" size={18} />
            <span>
              <small>연료</small>
              <b>72%</b>
            </span>
          </div>
          <div className="status-item">
            <Icon name="road" size={18} />
            <span>
              <small>누적 안전운전거리</small>
              <b>
                2,277 <em>km</em>
              </b>
            </span>
          </div>
        </div>
        <div className="weather">
          <span className="weather-icon">
            <Icon name="sun" size={23} />
          </span>
          <span>
            <small>오늘의 날씨</small>
            <b>24°</b>
            <em>맑음</em>
          </span>
        </div>
      </header>

      <div className="drive-layout">
        <section className="map-card">
          <div className="map-topline">
            <div className="map-brand-speed">
              <span className="map-record-status">
                <b>안전 주행 기록 중</b>
                <small>{driveTime} · 주행 데이터 저장 중</small>
              </span>
              <span className="map-speed">
                <b>58</b>
                <small>km/h</small>
              </span>
            </div>
            <div className="next-turn">
              <span className="turn-icon">↱</span>
              <span>
                <small>{routeState === "active" ? "180m 앞" : "320m 앞"}</small>
                <b>
                  {routeState === "active" ? `${destination} 방면 우회전` : "푸른대로 방면 우회전"}
                </b>
              </span>
            </div>
            <div className="arrival">
              <span>
                <small>도착 예정</small>
                <b>{routeState === "active" ? "10:41" : "10:48"}</b>
              </span>
              <i />
              <span>
                <small>남은 거리</small>
                <b>{routeState === "active" ? "8.7km" : "12.4km"}</b>
              </span>
            </div>
          </div>
          <div className="map-viewport">
            <div className="map-canvas" style={{ transform: `scale(${mapZoom})` }}>
              <svg
                viewBox="0 0 1200 600"
                preserveAspectRatio="xMidYMid slice"
                aria-label="주행 경로 지도"
              >
                <defs>
                  <pattern id="minorGrid" width="80" height="80" patternUnits="userSpaceOnUse">
                    <path d="M80 0H0v80" fill="none" stroke="#284054" strokeWidth="2" />
                  </pattern>
                  <filter id="routeGlow">
                    <feGaussianBlur stdDeviation="8" result="blur" />
                    <feMerge>
                      <feMergeNode in="blur" />
                      <feMergeNode in="SourceGraphic" />
                    </feMerge>
                  </filter>
                </defs>
                <rect width="1200" height="600" fill="#102332" />
                <rect width="1200" height="600" fill="url(#minorGrid)" opacity=".35" />
                <g stroke="#2e4658" strokeWidth="22" fill="none">
                  <path d="M-40 130 220 180 420 70 760 120 970 35 1250 110" />
                  <path d="M50 570 180 400 445 355 610 180 890 200 1190 85" />
                  <path d="M80-30l70 280 230 140 80 250M570-20l90 260 240 150 70 250M1020-20 900 200l110 190-30 250" />
                </g>
                <g stroke="#5a6f7c" strokeWidth="3" fill="none" opacity=".6">
                  <path d="M-30 125 220 175 420 65 760 115 970 30 1250 105" />
                  <path d="M50 565 180 395 445 350 610 175 890 195 1190 80" />
                </g>
                <path
                  d="M120 590c55-105 110-175 186-189 98-19 153 24 239-53 78-70 56-154 158-180 75-19 135 35 203-12 52-36 78-98 98-176"
                  stroke={routeState === "active" ? "#2d8cff" : "#48d7ef"}
                  strokeWidth="14"
                  fill="none"
                  strokeLinecap="round"
                  filter="url(#routeGlow)"
                />
                <path
                  d="M120 590c55-105 110-175 186-189 98-19 153 24 239-53 78-70 56-154 158-180 75-19 135 35 203-12 52-36 78-98 98-176"
                  stroke="#baf8ff"
                  strokeWidth="3"
                  fill="none"
                  strokeLinecap="round"
                />
                <g fill="#6c8290" fontSize="18" fontFamily="sans-serif">
                  <text x="210" y="145">
                    새봄로
                  </text>
                  <text x="755" y="92">
                    푸른대로
                  </text>
                  <text x="935" y="460">
                    한빛공원
                  </text>
                  <text x="345" y="520">
                    중앙마을
                  </text>
                </g>
                <g transform="translate(715 160)">
                  <circle r="31" fill="#102a3a" stroke="#75e8f2" strokeWidth="3" />
                  <path d="m-12 10 12-29 12 29L0 5Z" fill="#70e5f4" />
                </g>
                {routeState === "active" && (
                  <g transform="translate(1004 28)">
                    <path
                      d="M0 0c-18 0-31 13-31 30 0 23 31 54 31 54s31-31 31-54C31 13 18 0 0 0Z"
                      fill="#ff5d72"
                    />
                    <circle cy="29" r="10" fill="#fff" />
                  </g>
                )}
                <g transform="translate(546 345)">
                  <circle r="25" fill="#fff" stroke="#ec5563" strokeWidth="7" />
                  <text y="8" textAnchor="middle" fontSize="22" fontWeight="800" fill="#253442">
                    30
                  </text>
                </g>
                <g transform="translate(900 153)">
                  <path d="m0-27 28 50h-56Z" fill="#fff" stroke="#ec5563" strokeWidth="6" />
                  <path d="M0-12v18M0 13v1" stroke="#263c4b" strokeWidth="4" />
                </g>
                <g transform="translate(305 399)">
                  <circle r="24" fill="#172d3d" stroke="#ffbd4c" strokeWidth="4" />
                  <path d="M-10 8h20M-5 8V-8h10V8" stroke="#ffd47c" strokeWidth="4" />
                </g>
              </svg>
            </div>
            <div className="map-tools">
              <button
                onClick={() => setMapZoom((z) => Math.min(1.4, z + 0.12))}
                aria-label="지도 확대"
              >
                <Icon name="plus" size={22} />
              </button>
              <span />
              <button
                onClick={() => setMapZoom((z) => Math.max(0.8, z - 0.12))}
                aria-label="지도 축소"
              >
                <Icon name="minus" size={22} />
              </button>
            </div>
            <button className="recenter" aria-label="현재 위치로 이동">
              <Icon name="compass" size={22} />
            </button>
            <div className="road-name">
              <Icon name="location" size={16} />
              <b>푸른대로</b>
            </div>
            <button className="map-search-trigger" onClick={() => setSearchOpen(true)}>
              <Icon name="location" size={18} />
              <span>{destination || "어디로 갈까요?"}</span>
              <Icon name="chevron" size={17} />
            </button>
            {searchOpen && (
              <div className="destination-search">
                <header>
                  <b>목적지 검색</b>
                  <button onClick={() => setSearchOpen(false)}>×</button>
                </header>
                <label>
                  <span>출발</span>
                  <input value="현재 위치" readOnly />
                </label>
                <label>
                  <span>도착</span>
                  <input
                    autoFocus
                    value={destination}
                    onChange={(event) => setDestination(event.target.value)}
                    placeholder="장소, 주소를 입력하세요"
                  />
                </label>
                <button
                  className="route-search-button"
                  onClick={() => requestFastestRoute(destination)}
                >
                  <Icon name="arrow" size={17} /> 가장 빠른 경로 찾기
                </button>
                <h3>최근 검색한 목적지</h3>
                {["서울숲 주차장", "여의도 한강공원", "성수동 카페거리", "강남역 5번 출구"]
                  .filter((place) => !destination || place.includes(destination))
                  .map((place, index) => (
                    <button
                      className="recent-place"
                      key={place}
                      onClick={() => {
                        requestFastestRoute(place);
                      }}
                    >
                      <span>
                        <Icon name={index === 0 ? "home" : "history"} size={17} />
                      </span>
                      <span>
                        <b>{place}</b>
                        <small>
                          {index === 0 ? "서울 성동구 뚝섬로 273" : "최근 방문한 목적지"}
                        </small>
                      </span>
                      <Icon name="chevron" size={16} />
                    </button>
                  ))}
              </div>
            )}
            {routeState === "searching" && (
              <div className="route-status-toast searching">
                <i />
                <span>
                  <b>실시간 교통 분석 중</b>
                  <small>가장 빠른 경로를 찾고 있어요</small>
                </span>
              </div>
            )}
            {routeState === "active" && (
              <div className="route-status-toast">
                <Icon name="shield" size={18} />
                <span>
                  <b>최적 경로 안내 시작</b>
                  <small>교통 상황을 반영해 6분 단축</small>
                </span>
              </div>
            )}
          </div>
        </section>

        <section className="rules-card lower-card">
          <div className="panel-heading">
            <div>
              <span className="heading-icon">
                <Icon name="shield" size={20} />
              </span>
              <span>
                <b>앞으로 지켜야 할 교통법규</b>
              </span>
            </div>
          </div>
          <div className="rule-list">
            {rules.length ? (
              rules.map((rule) => (
                <div
                  className={`rule-item ${ruleVerdict?.id === rule.id ? "is-passing" : ""}`}
                  key={rule.id}
                >
                  <span className="rule-distance">{rule.distance}</span>
                  <span
                    className="rule-sign"
                    style={{ color: rule.color, borderColor: `${rule.color}55` }}
                  >
                    {rule.kind}
                  </span>
                  <span className="rule-copy">
                    <b>{rule.title}</b>
                  </span>
                  <Icon name="chevron" size={17} />
                  {ruleVerdict?.id === rule.id && (
                    <span className={`rule-verdict ${ruleVerdict.type}`}>
                      {ruleVerdict.type === "good" ? "○" : ruleVerdict.type === "close" ? "△" : "×"}
                    </span>
                  )}
                </div>
              ))
            ) : (
              <div className="all-clear">
                <span>
                  <Icon name="shield" size={25} />
                </span>
                <div>
                  <b>현재 경로는 안전해요</b>
                </div>
              </div>
            )}
          </div>
        </section>

        <section className="empty-card lower-card">
          <div className="quick-actions">
            {quickActions
              .map((id) => quickActionOptions.find((item) => item.id === id)!)
              .map(({ icon, label, id }) => (
                <button key={id} className={`action-${id}`} onClick={() => setActionPanel(id)}>
                  <span>
                    <Icon name={icon} size={22} />
                  </span>
                  <b>{label}</b>
                </button>
              ))}
          </div>
        </section>
      </div>
      <div className="drive-bottom">
        <AppNav onNavigate={onNavigate} active="driving" isDriving />
        <button className="end-drive-button" onClick={onEnd}>
          <span>■</span>
          <b>주행 종료</b>
        </button>
      </div>
      {actionPanel && (
        <DriveActionPanel feature={actionPanel} onClose={() => setActionPanel(null)} />
      )}
    </main>
  );
}

const driveRecords = [
  {
    date: "오늘 10:24",
    route: "성수동 → 여의도",
    distance: "18.4 km",
    duration: "32분 18초",
    xp: "+142 XP",
    score: 92,
    issues: 1,
  },
  {
    date: "6월 11일 18:42",
    route: "강남역 → 잠실",
    distance: "12.7 km",
    duration: "26분 04초",
    xp: "+98 XP",
    score: 88,
    issues: 2,
  },
  {
    date: "6월 9일 14:10",
    route: "서울역 → 북촌",
    distance: "9.2 km",
    duration: "18분 42초",
    xp: "+110 XP",
    score: 97,
    issues: 0,
  },
  {
    date: "6월 7일 08:35",
    route: "마포 → 판교",
    distance: "31.8 km",
    duration: "48분 11초",
    xp: "+204 XP",
    score: 90,
    issues: 1,
  },
];

function UtilityHeader({ title }: { title: string }) {
  return (
    <header className="utility-header">
      <div className="utility-brand">
        <span className="brand-mark">
          <Icon name="road" size={20} />
        </span>
        <span>
          <strong>Safe Town</strong>
          <small>{title}</small>
        </span>
      </div>
      <span className="utility-date">6월 12일 수요일 · 10:24</span>
    </header>
  );
}

function DrivingReturn({ onClick }: { onClick: () => void }) {
  return (
    <button className="driving-float utility-driving" onClick={onClick}>
      <span className="live-dot" />
      <span>
        <small>현재 주행 중</small>
        <b>내비게이션으로 돌아가기</b>
      </span>
      <Icon name="chevron" size={19} />
    </button>
  );
}

function DetailDrawer({
  title,
  subtitle,
  icon,
  onClose,
  children,
}: {
  title: string;
  subtitle: string;
  icon: IconName;
  onClose: () => void;
  children: React.ReactNode;
}) {
  return (
    <div className="feature-backdrop drawer-backdrop" onClick={onClose}>
      <aside className="detail-drawer" onClick={(event) => event.stopPropagation()}>
        <header>
          <div>
            <span>
              <Icon name={icon} size={22} />
            </span>
            <div>
              <small>{subtitle}</small>
              <h2>{title}</h2>
            </div>
          </div>
          <button onClick={onClose}>×</button>
        </header>
        <div className="detail-drawer-body">{children}</div>
      </aside>
    </div>
  );
}

function ProfileDetail({
  type,
  onClose,
  onNavigate,
  driverName,
  onDriverNameChange,
  onFriendVisit,
}: {
  type: string;
  onClose: () => void;
  onNavigate: (page: Page) => void;
  driverName: string;
  onDriverNameChange: (name: string) => void;
  onFriendVisit: (friend: (typeof friends)[number]) => void;
}) {
  const [visibility, setVisibility] = useState(true);
  const [draftName, setDraftName] = useState(driverName);
  const [saved, setSaved] = useState(false);
  if (type === "주행 기록") {
    return (
      <DetailDrawer title="주행 기록" subtitle="MY DRIVING" icon="history" onClose={onClose}>
        <div className="drawer-summary">
          <span>
            <b>184 km</b>
            <small>이번 달</small>
          </span>
          <span>
            <b>94점</b>
            <small>평균 점수</small>
          </span>
          <span>
            <b>+1,284</b>
            <small>획득 XP</small>
          </span>
        </div>
        <button className="drawer-primary" onClick={() => onNavigate("history")}>
          전체 주행히스토리 보기
        </button>
      </DetailDrawer>
    );
  }
  if (type === "업적 및 배지") {
    return (
      <DetailDrawer title="업적 및 배지" subtitle="COLLECTION" icon="shield" onClose={onClose}>
        <div className="drawer-progress">
          <span>
            <b>12</b> / 48
          </span>
          <i>
            <em style={{ width: "25%" }} />
          </i>
        </div>
        <div className="drawer-badges">
          {[
            "한강 드라이버",
            "무사고 100km",
            "친환경 주행",
            "새벽길 탐험가",
            "부산 방문",
            "연속 7일",
            "제주 오름",
            "야간 주행",
            "완벽한 정지",
            "에코 마스터",
            "장거리 여행",
            "비 오는 날",
            "안전벨트 지킴이",
            "서울 탐험가",
            "강원 산길",
            "바다 드라이브",
            "마을 건축가",
            "친구와 함께",
          ].map((badge, index) => (
            <article key={badge} className={index > 3 ? "locked" : ""}>
              <span>
                <Icon name={index > 3 ? "shield" : "sparkles"} size={22} />
              </span>
              <b>{badge}</b>
              <small>{index > 3 ? "진행 중" : "획득 완료"}</small>
            </article>
          ))}
        </div>
      </DetailDrawer>
    );
  }
  if (type === "마을 성장 기록") {
    return (
      <DetailDrawer
        title="마을 성장 기록"
        subtitle="VILLAGE TIMELINE"
        icon="home"
        onClose={onClose}
      >
        <div className="growth-timeline">
          {villageStages.map(([name], index) => (
            <article className={index === 0 ? "current" : index > 0 ? "locked" : ""} key={name}>
              <i>{index + 1}</i>
              <span>
                <b>{name}</b>
                <small>
                  {index === 0 ? "현재 단계 · 38% 성장" : `다음 단계까지 ${500 + index * 420} XP`}
                </small>
              </span>
              {index === 0 && <em>현재</em>}
            </article>
          ))}
        </div>
      </DetailDrawer>
    );
  }
  if (type === "친구 및 커뮤니티") {
    return (
      <DetailDrawer title="친구 및 커뮤니티" subtitle="CHACHA CREW" icon="users" onClose={onClose}>
        <div className="crew-card">
          <span>
            <Icon name="users" size={25} />
          </span>
          <div>
            <small>나의 크루</small>
            <h3>안전운전 서울</h3>
            <p>이번 주 공동 목표 72%</p>
          </div>
          <i>
            <em style={{ width: "72%" }} />
          </i>
        </div>
        <div className="action-list">
          {friends.map((friend) => (
            <button key={friend.name} onClick={() => onFriendVisit(friend)}>
              <span className="list-avatar">{friend.name[0]}</span>
              <span>
                <b>{friend.name}</b>
                <small>{friend.status}</small>
              </span>
              <em>마을 방문</em>
            </button>
          ))}
        </div>
      </DetailDrawer>
    );
  }
  return (
    <DetailDrawer title="내 프로필" subtitle="DRIVER PROFILE" icon="user" onClose={onClose}>
      <div className="profile-edit">
        <span className="large-avatar">차</span>
        <label>
          닉네임
          <input
            value={draftName}
            onChange={(event) => {
              setDraftName(event.target.value);
              setSaved(false);
            }}
          />
        </label>
        <label>
          운전자 칭호
          <select defaultValue="골드 드라이버">
            <option>골드 드라이버</option>
            <option>에코 드라이버</option>
            <option>무사고 마스터</option>
          </select>
        </label>
        <button className="setting-row" onClick={() => setVisibility((value) => !value)}>
          <span>
            <b>프로필 공개</b>
            <small>친구에게 마을과 기록을 공개합니다</small>
          </span>
          <i className={visibility ? "toggle-on" : ""}>
            <em />
          </i>
        </button>
        <button
          className="drawer-primary"
          onClick={() => {
            onDriverNameChange(draftName.trim() || driverName);
            setSaved(true);
          }}
        >
          {saved ? "저장 완료" : "변경사항 저장"}
        </button>
      </div>
    </DetailDrawer>
  );
}

function SettingsDetail({ type, onClose }: { type: string; onClose: () => void }) {
  const [states, setStates] = useState([true, true, false, true]);
  const toggle = (index: number) =>
    setStates((items) => items.map((value, i) => (i === index ? !value : value)));
  const rows: Record<string, string[]> = {
    "연결 차량 및 데이터 권한": [
      "차량 상태 읽기",
      "현재 위치 접근",
      "주행 기록 저장",
      "음성 서비스 접근",
    ],
    "주행 데이터 관리": ["자동 주행 기록", "안전점수 분석", "위반 이벤트 저장", "월간 리포트 생성"],
    "개인정보 및 보안": ["생체 인증", "앱 잠금", "위치 기록 보호", "친구 공개 범위"],
    "음성 데이터 및 AI 설정": [
      "온디바이스 음성 인식",
      "음성 기록 자동 삭제",
      "차차 AI 개인화",
      "음성 호출어 사용",
    ],
    "배지·컬렉션 알림 관리": [
      "새 지역 배지",
      "기간 한정 배지",
      "친구 획득 소식",
      "컬렉션 완성 알림",
    ],
  };
  if (type === "지도 다운로드 관리") {
    return (
      <DetailDrawer title={type} subtitle="OFFLINE MAP" icon="location" onClose={onClose}>
        <div className="storage-bar">
          <span>
            <b>3.2 GB</b>
            <small>지도 데이터 사용량</small>
          </span>
          <i>
            <em style={{ width: "46%" }} />
          </i>
        </div>
        <div className="download-list">
          {[
            ["서울·경기", "1.2 GB", "최신"],
            ["강원도", "680 MB", "업데이트"],
            ["충청도", "540 MB", "다운로드"],
            ["전라도", "720 MB", "다운로드"],
            ["경상도", "840 MB", "다운로드"],
            ["제주도", "310 MB", "최신"],
          ].map(([region, size, status]) => (
            <button key={region}>
              <Icon name="location" size={18} />
              <span>
                <b>{region}</b>
                <small>{size}</small>
              </span>
              <em>{status}</em>
            </button>
          ))}
        </div>
      </DetailDrawer>
    );
  }
  if (type === "화면 밝기 및 주·야간 모드") {
    return (
      <DetailDrawer title={type} subtitle="DISPLAY" icon="sun" onClose={onClose}>
        <div className="display-preview">
          <div>
            <span>10:24</span>
            <b>주간 모드</b>
          </div>
        </div>
        <label className="range-setting">
          <span>
            <b>화면 밝기</b>
            <em>78%</em>
          </span>
          <input type="range" defaultValue="78" />
        </label>
        <div className="mode-choices">
          <button className="active">
            <Icon name="sun" size={22} />
            주간
          </button>
          <button>
            <Icon name="clock" size={22} />
            야간
          </button>
          <button>
            <Icon name="settings" size={22} />
            자동
          </button>
        </div>
      </DetailDrawer>
    );
  }
  if (type === "운전자 프로필 전환") {
    return (
      <DetailDrawer title={type} subtitle="DRIVER" icon="users" onClose={onClose}>
        <div className="driver-profiles">
          {["안전운전 마스터", "김민준", "게스트 모드"].map((name, index) => (
            <button className={index === 0 ? "active" : ""} key={name}>
              <span>{name[0]}</span>
              <div>
                <b>{name}</b>
                <small>{index === 0 ? "현재 프로필" : "개인 설정 불러오기"}</small>
              </div>
              {index === 0 && <em>사용 중</em>}
            </button>
          ))}
        </div>
        <button className="drawer-primary">새 운전자 추가</button>
      </DetailDrawer>
    );
  }
  if (type === "고객센터 / FAQ") {
    return (
      <DetailDrawer title={type} subtitle="SUPPORT" icon="message" onClose={onClose}>
        <div className="faq-list">
          {[
            "차량 데이터가 보이지 않아요",
            "안전점수는 어떻게 계산하나요?",
            "배지를 획득하지 못했어요",
            "내비게이션 연결이 끊겼어요",
          ].map((question) => (
            <button key={question}>
              <span>{question}</span>
              <Icon name="chevron" size={17} />
            </button>
          ))}
        </div>
        <div className="support-actions">
          <button>
            <Icon name="message" size={19} />
            1:1 문의
          </button>
          <button>
            <Icon name="phone" size={19} />
            전화 상담
          </button>
        </div>
      </DetailDrawer>
    );
  }
  if (type === "앱 정보 / 버전") {
    return (
      <DetailDrawer title="앱 정보" subtitle="ABOUT" icon="settings" onClose={onClose}>
        <div className="about-app">
          <span className="brand-mark">
            <Icon name="road" size={30} />
          </span>
          <h2>Safe Town</h2>
          <p>안전운전으로 함께 성장하는 마을</p>
          <b>Version 1.0.0 · Pleos Connect</b>
        </div>
        <div className="faq-list">
          {["이용약관", "개인정보 처리방침", "오픈소스 라이선스", "업데이트 확인"].map((item) => (
            <button key={item}>
              <span>{item}</span>
              <Icon name="chevron" size={17} />
            </button>
          ))}
        </div>
      </DetailDrawer>
    );
  }
  const labels = rows[type] ?? ["기능 사용", "알림 허용", "데이터 자동 동기화", "개인화 추천"];
  return (
    <DetailDrawer title={type} subtitle="DETAILED SETTINGS" icon="settings" onClose={onClose}>
      <div className="setting-detail-intro">
        <Icon name="shield" size={24} />
        <div>
          <b>안전하게 관리되고 있어요</b>
          <small>차량에서 언제든 변경할 수 있습니다.</small>
        </div>
      </div>
      {labels.map((label, index) => (
        <button className="setting-row" key={label} onClick={() => toggle(index)}>
          <span>
            <b>{label}</b>
            <small>
              {index === 0 ? "서비스 제공에 필요한 기본 설정" : "앱 사용 환경에 맞게 설정합니다"}
            </small>
          </span>
          <i className={states[index] ? "toggle-on" : ""}>
            <em />
          </i>
        </button>
      ))}
      {type === "주행 데이터 관리" && (
        <>
          <div className="data-usage">
            <span>
              <b>248개</b>
              <small>저장된 주행</small>
            </span>
            <span>
              <b>84.2 MB</b>
              <small>사용 공간</small>
            </span>
          </div>
          <div className="danger-actions">
            <button>주행 데이터 내보내기</button>
            <button>모든 기록 삭제</button>
          </div>
        </>
      )}
    </DetailDrawer>
  );
}

function HistoryPage({
  onNavigate,
  isDriving,
  onStartDrive,
}: {
  onNavigate: (page: Page) => void;
  isDriving: boolean;
  onStartDrive: () => void;
}) {
  const [selected, setSelected] = useState(0);
  const record = driveRecords[selected];
  return (
    <main className="utility-page screen-enter">
      <UtilityHeader title="주행히스토리" />
      <div className="history-layout">
        <section className="history-list-panel">
          <div className="utility-title">
            <small>최근 30일</small>
            <h1>주행 기록</h1>
            <p>총 184 km · 평균 안전점수 92점</p>
          </div>
          <div className="drive-record-list">
            {driveRecords.map((item, index) => (
              <button
                key={item.date}
                className={selected === index ? "active" : ""}
                onClick={() => setSelected(index)}
              >
                <span className="record-score">{item.score}</span>
                <span>
                  <small>{item.date}</small>
                  <b>{item.route}</b>
                  <em>
                    {item.duration} · {item.distance} · {item.xp}
                  </em>
                </span>
                <Icon name="chevron" size={18} />
              </button>
            ))}
          </div>
        </section>
        <section className="history-detail-panel">
          <div className="detail-head">
            <span>
              <small>{record.date}</small>
              <h2>{record.route}</h2>
            </span>
            <strong>
              {record.score}
              <small>점</small>
            </strong>
          </div>
          <div className="detail-stats">
            <div>
              <small>주행 거리</small>
              <b>{record.distance}</b>
            </div>
            <div>
              <small>주행 시간</small>
              <b>{record.duration}</b>
            </div>
            <div>
              <small>획득 경험치</small>
              <b>{record.xp}</b>
            </div>
          </div>
          <h3>점수 및 경험치 변동</h3>
          <div className="violation-list">
            <article>
              <span className="traffic-sign speed-sign">30</span>
              <div>
                <b>어린이 보호구역 안전 통과</b>
                <small>제한속도를 안정적으로 유지했어요</small>
              </div>
              <em className="plus-score">+20 XP</em>
            </article>
            {record.issues > 0 && (
              <article>
                <span className="traffic-sign light-sign">●</span>
                <div>
                  <b>황색 신호 진입</b>
                  <small>정지선 전 감속이 늦었어요</small>
                </div>
                <em>-8점 · -15 XP</em>
              </article>
            )}
            {record.issues > 1 && (
              <article>
                <span className="traffic-sign">↘</span>
                <div>
                  <b>차로 변경 시 방향지시등 지연</b>
                  <small>차로 변경 3초 전에 알려주세요</small>
                </div>
                <em>-4점 · -8 XP</em>
              </article>
            )}
          </div>
        </section>
      </div>
      <div className="utility-bottom-nav">
        <AppNav
          onNavigate={onNavigate}
          active="history"
          isDriving={isDriving}
          onStartDrive={onStartDrive}
        />
      </div>
    </main>
  );
}

function ProfilePage({
  onNavigate,
  isDriving,
  onStartDrive,
  driverName,
  onDriverNameChange,
  onFriendVisit,
}: {
  onNavigate: (page: Page) => void;
  isDriving: boolean;
  onStartDrive: () => void;
  driverName: string;
  onDriverNameChange: (name: string) => void;
  onFriendVisit: (friend: (typeof friends)[number]) => void;
}) {
  const [selectedPart, setSelectedPart] = useState<VehiclePart>("coolant");
  const [selectedPartKey, setSelectedPartKey] = useState("coolant");
  const [profileDetail, setProfileDetail] = useState<string | null>(null);
  const menus = [
    ["user", "내 프로필", "닉네임, 프로필 사진, 운전자 칭호"],
    ["history", "주행 기록", "월별·주별 주행 기록과 안전운전 점수"],
    ["shield", "업적 및 배지", "누적 안전주행 기록과 지역 컬렉션"],
    ["home", "마을 성장 기록", "레벨, 누적 획득 XP, 건물 발전 내역"],
    ["users", "친구 및 커뮤니티", "친구 목록, 크루, 공동 목표"],
  ];
  return (
    <main className="utility-page screen-enter">
      <UtilityHeader title="마이페이지" />
      <div className="profile-layout">
        <section className="profile-main">
          <div className="profile-hero">
            <span className="profile-avatar">
              <Icon name="user" size={28} />
            </span>
            <div>
              <small>골드 드라이버</small>
              <h1>{driverName}</h1>
              <p>마을 레벨 1 · 프로필 공개</p>
            </div>
            <div className="profile-metrics">
              <span>
                <b>2,277</b>
                <small>누적 안전운전 km</small>
              </span>
              <span>
                <b>94</b>
                <small>안전운전 점수</small>
              </span>
              <span>
                <b>Lv. 1</b>
                <small>마을 레벨</small>
              </span>
            </div>
          </div>
          <div className="profile-menu">
            {menus.map(([icon, title, desc]) => (
              <button key={title} onClick={() => setProfileDetail(title)}>
                <Icon name={icon as IconName} size={19} />
                <span>
                  <b>{title}</b>
                  <small>{desc}</small>
                </span>
                <Icon name="chevron" size={17} />
              </button>
            ))}
          </div>
        </section>
        <aside className="vehicle-card">
          <div className="vehicle-title">
            <span>
              <Icon name="car" size={23} />
            </span>
            <div>
              <small>연결된 차량</small>
              <h2>차차 EV 2025</h2>
            </div>
            <em>정상</em>
          </div>
          <div className="car-visual">
            <ThreeVehicle selected={selectedPart} />
            <span>선택 부위가 3D 차량에서 강조됩니다 · 24가 3860</span>
          </div>
          <h3>차량 부품 상태</h3>
          <div className="part-list">
            {[
              ["engine-oil", "coolant", "엔진오일", "교체까지 7,640km", "76%"],
              ["brake-fluid", "brake", "브레이크액", "교체까지 15,614km", "61%"],
              ["coolant", "coolant", "냉각수", "교체까지 75,614km", "76%"],
              ["aircon-filter", "battery", "에어컨필터", "교체까지 7,640km", "76%"],
              ["brake", "brake", "브레이크패드", "교체까지 15,614km", "61%"],
              ["battery", "battery", "자동차 배터리", "교체까지 2년 4개월", "58%"],
              ["tire", "tire", "타이어", "교체까지 25,614km", "51%"],
              ["engine-air", "battery", "엔진에어필터", "교체까지 15,614km", "61%"],
              ["mission-oil", "coolant", "미션오일", "교체까지 55,614km", "70%"],
              ["spark-plug", "battery", "점화플러그", "교체까지 55,614km", "70%"],
              ["fuel-filter", "battery", "연료필터", "교체까지 25,614km", "51%"],
            ].map(([key, focus, label, value, width]) => (
              <button
                key={key}
                className={selectedPartKey === key ? "active" : ""}
                onClick={() => {
                  setSelectedPart(focus as VehiclePart);
                  setSelectedPartKey(key);
                }}
              >
                <span>{label}</span>
                <b>{value}</b>
                <i>
                  <em style={{ width }} />
                </i>
              </button>
            ))}
          </div>
        </aside>
      </div>
      <div className="utility-bottom-nav">
        <AppNav
          onNavigate={onNavigate}
          active="profile"
          isDriving={isDriving}
          onStartDrive={onStartDrive}
        />
      </div>
      {profileDetail && (
        <ProfileDetail
          type={profileDetail}
          onClose={() => setProfileDetail(null)}
          onNavigate={onNavigate}
          driverName={driverName}
          onDriverNameChange={onDriverNameChange}
          onFriendVisit={onFriendVisit}
        />
      )}
    </main>
  );
}

function SettingsPage({
  onNavigate,
  isDriving,
  onStartDrive,
}: {
  onNavigate: (page: Page) => void;
  isDriving: boolean;
  onStartDrive: () => void;
}) {
  const [toggles, setToggles] = useState([true, true, true, true, false, true, true, false]);
  const [settingDetail, setSettingDetail] = useState<string | null>(null);
  const toggle = (index: number) =>
    setToggles((items) => items.map((value, i) => (i === index ? !value : value)));
  const items = [
    ["음성 안내", "주행 중 필요한 안내 음성"],
    ["효과음", "정차 후 게임 효과음"],
    ["알림", "업적, 마을 성장, 친구 활동 알림"],
    ["제한속도 경고", "현재 도로 제한속도 초과 시 음성과 화면으로 안내"],
    ["교통법규 자동 판정", "신호와 표지판 통과 결과를 자동으로 기록"],
    ["차량 상태 자동 점검", "시동 후 차량 안전 상태와 관리 항목 확인"],
    ["위치 기반 배지 알림", "새로운 지역 진입 시 컬렉션 획득 가능 여부 안내"],
    ["안전운전 기록 공개", "다른 이용자에게 인증 기록 공개 여부"],
  ];
  return (
    <main className="utility-page screen-enter">
      <UtilityHeader title="설정" />
      <section className="settings-panel">
        <div className="utility-title">
          <small>차량과 앱을 나에게 맞게</small>
          <h1>설정</h1>
        </div>
        <h3>주행 환경</h3>
        {items.slice(0, 7).map(([title, desc], index) => (
          <button className="setting-row" key={title} onClick={() => toggle(index)}>
            <span>
              <b>{title}</b>
              <small>{desc}</small>
            </span>
            <i className={toggles[index] ? "toggle-on" : ""}>
              <em />
            </i>
          </button>
        ))}
        <h3>개인정보 및 계정</h3>
        <button className="setting-row" onClick={() => toggle(7)}>
          <span>
            <b>{items[7][0]}</b>
            <small>{items[7][1]}</small>
          </span>
          <i className={toggles[7] ? "toggle-on" : ""}>
            <em />
          </i>
        </button>
        {[
          "연결 차량 및 데이터 권한",
          "주행 데이터 관리",
          "개인정보 및 보안",
          "음성 데이터 및 AI 설정",
          "배지·컬렉션 알림 관리",
          "화면 밝기 및 주·야간 모드",
          "운전자 프로필 전환",
          "고객센터 / FAQ",
          "앱 정보 / 버전",
        ].map((label) => (
          <button className="setting-row link" key={label} onClick={() => setSettingDetail(label)}>
            <span>
              <b>{label}</b>
            </span>
            <Icon name="chevron" size={17} />
          </button>
        ))}
      </section>
      <div className="utility-bottom-nav">
        <AppNav
          onNavigate={onNavigate}
          active="settings"
          isDriving={isDriving}
          onStartDrive={onStartDrive}
        />
      </div>
      {settingDetail && (
        <SettingsDetail type={settingDetail} onClose={() => setSettingDetail(null)} />
      )}
    </main>
  );
}

function DriveResult({ onHome }: { onHome: () => void }) {
  return (
    <main className="result-screen screen-enter">
      <div className="result-glow" />
      <section className="result-card">
        <header>
          <span className="result-check">
            <Icon name="shield" size={31} />
          </span>
          <div>
            <small>주행이 안전하게 종료되었습니다</small>
            <h1>오늘의 주행 결과</h1>
          </div>
          <strong>
            92<small>점</small>
          </strong>
        </header>
        <div className="result-summary">
          <div>
            <small>주행 거리</small>
            <b>18.4 km</b>
          </div>
          <div>
            <small>주행 시간</small>
            <b>32분 18초</b>
          </div>
          <div>
            <small>최종 경험치</small>
            <b>+142 XP</b>
          </div>
        </div>
        <div className="result-body">
          <section>
            <h2>경험치 및 점수 내역</h2>
            <article className="reward-row positive">
              <span className="traffic-sign speed-sign">30</span>
              <div>
                <b>어린이 보호구역 안전 통과</b>
                <small>제한속도를 잘 지켰어요</small>
              </div>
              <em>+20 XP</em>
            </article>
            <article className="reward-row negative">
              <span className="traffic-sign light-sign">●</span>
              <div>
                <b>황색 신호 진입</b>
                <small>정지선 전 감속이 늦었어요</small>
              </div>
              <em>-8점 · -15 XP</em>
            </article>
            <article className="reward-row positive">
              <span className="traffic-sign">
                <Icon name="shield" size={18} />
              </span>
              <div>
                <b>급가속·급제동 없이 주행</b>
                <small>편안한 주행을 유지했어요</small>
              </div>
              <em>+35 XP</em>
            </article>
          </section>
          <aside className="badge-reward">
            <small>지역 컬렉션</small>
            <h2>새로운 배지 획득</h2>
            <div className="big-badge">
              <Icon name="sparkles" size={42} />
              <i />
            </div>
            <b>서울 한강 드라이버</b>
            <p>한강대교 지역을 처음 방문했어요</p>
            <span>COLLECTION 12 / 48</span>
          </aside>
        </div>
        <button className="result-home" onClick={onHome}>
          보상 받고 마을로 돌아가기 <Icon name="chevron" size={18} />
        </button>
      </section>
    </main>
  );
}

export default function App() {
  const [page, setPage] = useState<Page>("village");
  const [isDriving, setIsDriving] = useState(false);
  const [elapsed, setElapsed] = useState(0);
  const [quickActions, setQuickActions] = useState(["phone", "message", "music", "ai"]);
  const [driverName, setDriverName] = useState("안전운전 마스터");
  const [vehicleCheckSeen, setVehicleCheckSeen] = useState(false);
  const [friendVisit, setFriendVisit] = useState<(typeof friends)[number] | null>(null);

  const startDrive = () => {
    setIsDriving(true);
    setElapsed(0);
    setPage("driving");
  };

  useEffect(() => {
    if (!isDriving) return;
    const timer = window.setInterval(() => setElapsed((value) => value + 1), 1000);
    return () => window.clearInterval(timer);
  }, [isDriving]);

  const endDrive = () => {
    setIsDriving(false);
    setPage("result");
  };

  if (page === "driving" && isDriving) {
    return (
      <DrivingScreen
        elapsed={elapsed}
        onEnd={endDrive}
        onNavigate={setPage}
        quickActions={quickActions}
      />
    );
  }
  if (page === "result") return <DriveResult onHome={() => setPage("village")} />;
  if (page === "history")
    return <HistoryPage onNavigate={setPage} isDriving={isDriving} onStartDrive={startDrive} />;
  if (page === "profile")
    return (
      <ProfilePage
        onNavigate={setPage}
        isDriving={isDriving}
        onStartDrive={startDrive}
        driverName={driverName}
        onDriverNameChange={setDriverName}
        onFriendVisit={(friend) => {
          setFriendVisit(friend);
          setPage("village");
        }}
      />
    );
  if (page === "settings")
    return <SettingsPage onNavigate={setPage} isDriving={isDriving} onStartDrive={startDrive} />;
  return (
    <VillageScreen
      onStart={startDrive}
      isDriving={isDriving}
      onResume={() => setPage("driving")}
      onNavigate={setPage}
      showStartupCheck={!vehicleCheckSeen}
      onStartupCheckShown={() => setVehicleCheckSeen(true)}
      friendVisit={friendVisit}
      onReturnOwnVillage={() => setFriendVisit(null)}
    />
  );
}
