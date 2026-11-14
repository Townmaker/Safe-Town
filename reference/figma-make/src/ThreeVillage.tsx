import { useEffect, useRef } from "react";
import * as THREE from "three";

type Props = {
  era: number;
  onTap?: () => void;
};

const colors = {
  grass: 0x4d9361,
  darkGrass: 0x2f6548,
  earth: 0x8a6848,
  wood: 0x5e3d27,
  cream: 0xe8ddbd,
  roof: 0x263d43,
  cyan: 0x58dce7,
};

function material(color: number, extra: Partial<THREE.MeshStandardMaterialParameters> = {}) {
  return new THREE.MeshStandardMaterial({ color, roughness: 0.72, metalness: 0.04, ...extra });
}

function box(
  parent: THREE.Object3D,
  size: [number, number, number],
  position: [number, number, number],
  color: number,
  rotation: [number, number, number] = [0, 0, 0],
) {
  const mesh = new THREE.Mesh(new THREE.BoxGeometry(...size), material(color));
  mesh.position.set(...position);
  mesh.rotation.set(...rotation);
  mesh.castShadow = true;
  mesh.receiveShadow = true;
  parent.add(mesh);
  return mesh;
}

function cylinder(
  parent: THREE.Object3D,
  radiusTop: number,
  radiusBottom: number,
  height: number,
  position: [number, number, number],
  color: number,
  segments = 16,
) {
  const mesh = new THREE.Mesh(
    new THREE.CylinderGeometry(radiusTop, radiusBottom, height, segments),
    material(color),
  );
  mesh.position.set(...position);
  mesh.castShadow = true;
  mesh.receiveShadow = true;
  parent.add(mesh);
  return mesh;
}

function addTree(parent: THREE.Object3D, x: number, z: number, scale = 1) {
  const group = new THREE.Group();
  cylinder(group, 0.08 * scale, 0.1 * scale, 0.65 * scale, [0, 0.32 * scale, 0], 0x6d4930);
  const crown = new THREE.Mesh(new THREE.IcosahedronGeometry(0.38 * scale, 1), material(0x67b96d));
  crown.position.y = 0.78 * scale;
  crown.castShadow = true;
  group.add(crown);
  group.position.set(x, 0.18, z);
  parent.add(group);
}

function addFlower(parent: THREE.Object3D, x: number, z: number, color: number) {
  const stem = cylinder(parent, 0.012, 0.015, 0.16, [x, 0.38, z], 0x4c9b55, 6);
  stem.castShadow = false;
  const bloom = new THREE.Mesh(
    new THREE.SphereGeometry(0.055, 8, 8),
    material(color, { emissive: color, emissiveIntensity: 0.08 }),
  );
  bloom.position.set(x, 0.48, z);
  parent.add(bloom);
}

function addCloud(
  parent: THREE.Object3D,
  x: number,
  y: number,
  z: number,
  animations: ((time: number) => void)[],
  phase: number,
) {
  const cloud = new THREE.Group();
  for (const [cx, cy, scale] of [
    [-0.45, 0, 0.48],
    [0, 0.12, 0.68],
    [0.52, 0, 0.42],
    [0.08, -0.05, 0.56],
  ]) {
    const puff = new THREE.Mesh(
      new THREE.SphereGeometry(scale, 16, 12),
      material(0xffffff, { transparent: true, opacity: 0.88, roughness: 1 }),
    );
    puff.position.set(cx, cy, 0);
    cloud.add(puff);
  }
  cloud.position.set(x, y, z);
  parent.add(cloud);
  animations.push((time) => {
    cloud.position.x = x + Math.sin(time * 0.08 + phase) * 1.4;
    cloud.position.y = y + Math.sin(time * 0.25 + phase) * 0.08;
  });
}

function addPerson(
  parent: THREE.Object3D,
  x: number,
  z: number,
  shirt: number,
  animations: ((time: number) => void)[],
  phase: number,
) {
  const person = new THREE.Group();
  cylinder(person, 0.09, 0.09, 0.34, [0, 0.34, 0], shirt);
  const head = new THREE.Mesh(new THREE.SphereGeometry(0.11, 12, 12), material(0xf0bd8e));
  head.position.y = 0.64;
  head.castShadow = true;
  person.add(head);
  box(person, [0.06, 0.3, 0.06], [-0.06, 0.05, 0], 0x283a48);
  box(person, [0.06, 0.3, 0.06], [0.06, 0.05, 0], 0x283a48);
  person.position.set(x, 0.22, z);
  parent.add(person);
  animations.push((time) => {
    person.position.x = x + Math.sin(time * 0.55 + phase) * 0.65;
    person.position.z = z + Math.cos(time * 0.55 + phase) * 0.24;
    person.rotation.y = Math.sin(time * 0.55 + phase) > 0 ? Math.PI / 2 : -Math.PI / 2;
  });
}

function addHut(
  parent: THREE.Object3D,
  x: number,
  z: number,
  rotation: number,
  animations: ((time: number) => void)[],
) {
  const hut = new THREE.Group();
  cylinder(hut, 0.65, 0.72, 0.75, [0, 0.55, 0], 0xb78b58, 12);
  const roof = new THREE.Mesh(
    new THREE.ConeGeometry(0.92, 0.75, 16),
    material(0x6f4c2c, { roughness: 1 }),
  );
  roof.position.y = 1.25;
  roof.castShadow = true;
  hut.add(roof);
  box(hut, [0.26, 0.55, 0.05], [0, 0.42, 0.71], 0x39271e);
  const smokeGroup = new THREE.Group();
  for (let i = 0; i < 3; i++) {
    const smoke = new THREE.Mesh(
      new THREE.SphereGeometry(0.12 + i * 0.04, 10, 10),
      material(0xcbd5cc, { transparent: true, opacity: 0.32 }),
    );
    smoke.position.set(0.22 + i * 0.05, 1.75 + i * 0.22, 0);
    smokeGroup.add(smoke);
  }
  hut.add(smokeGroup);
  hut.position.set(x, 0.2, z);
  hut.rotation.y = rotation;
  parent.add(hut);
  animations.push((time) => {
    smokeGroup.children.forEach((smoke, i) => {
      smoke.position.y = 1.75 + i * 0.22 + ((time * 0.15 + i * 0.24) % 0.5);
      smoke.scale.setScalar(0.8 + ((time * 0.15 + i * 0.2) % 0.45));
    });
  });
}

function addHanok(parent: THREE.Object3D, x: number, z: number, scale = 1) {
  const home = new THREE.Group();
  box(home, [1.6, 0.68, 1.12], [0, 0.62, 0], colors.cream);
  for (const px of [-0.7, 0.7]) {
    for (const pz of [-0.46, 0.46])
      cylinder(home, 0.055, 0.055, 0.9, [px, 0.62, pz], colors.wood, 8);
  }
  const roof = new THREE.Mesh(new THREE.ConeGeometry(1.35, 0.52, 4), material(colors.roof));
  roof.position.y = 1.25;
  roof.rotation.y = Math.PI / 4;
  roof.scale.z = 0.78;
  roof.castShadow = true;
  home.add(roof);
  box(home, [0.42, 0.58, 0.04], [0, 0.58, 0.58], 0x6d3928);
  home.position.set(x, 0.2, z);
  home.scale.setScalar(scale);
  parent.add(home);
}

function addModernHouse(parent: THREE.Object3D, x: number, z: number, style: number, scale = 1) {
  const home = new THREE.Group();
  const wallColors = [0xc97758, 0xf0e1c2, 0xd7dfd8];
  const width = style === 2 ? 2.1 : 1.55;
  box(home, [width, 0.9, 1.25], [0, 0.7, 0], wallColors[Math.min(style, 2)]);
  if (style < 2) {
    const roof = new THREE.Mesh(
      new THREE.ConeGeometry(style === 0 ? 1.3 : 1.15, 0.55, 4),
      material(style === 0 ? 0x7e4532 : 0x395f65),
    );
    roof.position.y = 1.43;
    roof.rotation.y = Math.PI / 4;
    roof.scale.z = 0.78;
    roof.castShadow = true;
    home.add(roof);
  } else {
    box(home, [width + 0.12, 0.12, 1.37], [0, 1.22, 0], 0x537b79);
  }
  for (const wx of [-0.48, 0.48]) {
    box(home, [0.28, 0.32, 0.04], [wx, 0.74, 0.65], 0x73c7d5);
  }
  box(home, [0.34, 0.62, 0.05], [0, 0.51, 0.66], 0x695042);
  home.position.set(x, 0.2, z);
  home.scale.setScalar(scale);
  parent.add(home);
}

function addVillageWalls(parent: THREE.Object3D, wood = true) {
  const radius = 4.85;
  for (let i = 0; i < 48; i++) {
    const angle = (i / 48) * Math.PI * 2;
    if (Math.abs(Math.sin(angle)) < 0.12 && Math.cos(angle) > 0) continue;
    cylinder(
      parent,
      wood ? 0.055 : 0.08,
      wood ? 0.07 : 0.1,
      wood ? 0.72 : 0.82,
      [Math.cos(angle) * radius, wood ? 0.58 : 0.61, Math.sin(angle) * radius],
      wood ? 0x845c36 : 0xd5d1bf,
      wood ? 7 : 10,
    );
  }
  for (const angle of [0.62, 2.2, 3.75, 5.25]) {
    const tower = new THREE.Group();
    cylinder(tower, 0.34, 0.4, 1.15, [0, 0.73, 0], wood ? 0x9b7042 : 0xded9c8, 10);
    const roof = new THREE.Mesh(
      new THREE.ConeGeometry(0.55, 0.55, wood ? 10 : 6),
      material(wood ? 0x6b462c : 0xb85f42),
    );
    roof.position.y = 1.55;
    roof.castShadow = true;
    tower.add(roof);
    tower.position.set(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
    parent.add(tower);
  }
  box(parent, [0.75, 0.12, 2.7], [5.05, 0.38, 0], wood ? 0x9a744b : 0xe1ddcf);
}

function addMarket(parent: THREE.Object3D, x: number, z: number, color: number) {
  const stall = new THREE.Group();
  box(stall, [0.9, 0.08, 0.6], [0, 0.42, 0], 0x8f633a);
  for (const px of [-0.38, 0.38]) {
    for (const pz of [-0.22, 0.22]) box(stall, [0.04, 0.75, 0.04], [px, 0.76, pz], 0x70482e);
  }
  box(stall, [1.05, 0.08, 0.78], [0, 1.16, 0], color);
  for (let i = 0; i < 5; i++) {
    const goods = new THREE.Mesh(
      new THREE.SphereGeometry(0.07, 7, 7),
      material(i % 2 ? 0xe9b340 : 0xd96053),
    );
    goods.position.set(-0.28 + i * 0.14, 0.53, 0);
    stall.add(goods);
  }
  stall.position.set(x, 0.2, z);
  parent.add(stall);
}

function addTrain(parent: THREE.Object3D, animations: ((time: number) => void)[]) {
  box(parent, [10, 0.07, 0.09], [0, 0.27, -3.35], 0x6d7d82);
  box(parent, [10, 0.04, 0.06], [0, 0.27, -3.62], 0x6d7d82);
  const train = new THREE.Group();
  for (let i = 0; i < 3; i++) {
    const car = box(train, [0.95, 0.48, 0.45], [-i * 1.05, 0.53, 0], i === 0 ? 0xf2b84b : 0xe6e3d8);
    car.material = material(i === 0 ? 0xe9ad35 : 0xdce7e3, { metalness: 0.18 });
    box(train, [0.58, 0.16, 0.02], [-i * 1.05, 0.58, 0.236], 0x3b7482);
  }
  train.position.z = -3.48;
  parent.add(train);
  animations.push((time) => {
    train.position.x = ((time * 0.9 + 6) % 15) - 7.5;
  });
}

function addBoat(parent: THREE.Object3D, animations: ((time: number) => void)[], offset = 0) {
  const boat = new THREE.Group();
  const hull = new THREE.Mesh(new THREE.ConeGeometry(0.34, 1.15, 4), material(0x805435));
  hull.rotation.z = Math.PI / 2;
  hull.rotation.y = Math.PI / 4;
  boat.add(hull);
  box(boat, [0.05, 0.7, 0.05], [0, 0.37, 0], 0x5d4631);
  const sail = new THREE.Mesh(new THREE.ConeGeometry(0.32, 0.65, 3), material(0xf2d69a));
  sail.position.set(0, 0.55, 0);
  sail.rotation.z = -0.2;
  boat.add(sail);
  boat.position.y = 0.34;
  parent.add(boat);
  animations.push((time) => {
    const angle = time * 0.2 + offset;
    boat.position.x = Math.cos(angle) * 4.9;
    boat.position.z = Math.sin(angle) * 4.9;
    boat.rotation.y = -angle;
  });
}

function addCity(parent: THREE.Object3D, animations: ((time: number) => void)[], tier = 1) {
  const placements = [
    [-2.8, -1.2, 1.2, 2.7, 1.2, 0x5b9caf],
    [-1.35, -1.8, 1.0, 3.8, 1.0, 0x77bac6],
    [2.45, -1.5, 1.25, 4.6, 1.15, 0x438399],
    [3.1, 0.3, 1.0, 2.9, 1.0, 0x8bb7b4],
    [1.25, -2.1, 0.8, 3.2, 0.9, 0x5b95aa],
    [-3.1, 1.25, 1.0, 2.25, 1.0, 0x749e9e],
  ] as const;
  placements.forEach(([x, z, w, h, d, color], index) => {
    const adjustedHeight = h * (0.58 + tier * 0.24);
    const building = box(parent, [w, adjustedHeight, d], [x, adjustedHeight / 2 + 0.2, z], color);
    building.material = material(color, { metalness: 0.24, roughness: 0.35 });
    for (let y = 0.65; y < adjustedHeight; y += 0.45) {
      box(parent, [w + 0.015, 0.08, d + 0.02], [x, y, z], index % 2 ? 0x9ce8e6 : 0xffdc89);
    }
  });
  const plane = new THREE.Group();
  box(plane, [1.05, 0.13, 0.18], [0, 0, 0], 0xe7f0ee);
  box(plane, [0.28, 0.04, 1.1], [-0.05, 0, 0], 0x78dce4);
  plane.position.y = 4.8;
  parent.add(plane);
  animations.push((time) => {
    plane.position.x = Math.cos(time * 0.23) * 6;
    plane.position.z = Math.sin(time * 0.23) * 5;
    plane.rotation.y = -time * 0.23;
  });
  addTrain(parent, animations);
}

function addFloatingDistrict(parent: THREE.Object3D, animations: ((time: number) => void)[]) {
  for (let i = 0; i < 5; i++) {
    const island = new THREE.Group();
    const rock = new THREE.Mesh(
      new THREE.ConeGeometry(1.05 - i * 0.05, 1.4, 10),
      material(0x65828a),
    );
    rock.rotation.z = Math.PI;
    island.add(rock);
    cylinder(island, 1.02, 1.02, 0.18, [0, 0.73, 0], 0x68b979, 18);
    cylinder(island, 0.35, 0.42, 1.5 + i * 0.15, [0, 1.55, 0], 0xaad8d8, 12);
    const ring = new THREE.Mesh(
      new THREE.TorusGeometry(0.62, 0.06, 8, 28),
      material(0x62e7e6, { emissive: 0x62e7e6, emissiveIntensity: 0.8 }),
    );
    ring.rotation.x = Math.PI / 2;
    ring.position.y = 1.55;
    island.add(ring);
    const angle = (i / 5) * Math.PI * 2;
    island.position.set(Math.cos(angle) * 3.5, 1.1 + (i % 2) * 0.7, Math.sin(angle) * 3.5);
    parent.add(island);
    animations.push((time) => {
      island.position.y = 1.1 + (i % 2) * 0.7 + Math.sin(time * 0.7 + i) * 0.12;
      ring.rotation.z = time * 0.25;
    });
  }
}

function addSpaceColony(parent: THREE.Object3D, animations: ((time: number) => void)[]) {
  const planet = new THREE.Mesh(
    new THREE.SphereGeometry(4.8, 48, 48),
    material(0x295b6a, { roughness: 0.9 }),
  );
  planet.position.y = -4.25;
  planet.receiveShadow = true;
  parent.add(planet);
  for (const [x, z, size] of [
    [-1.8, 0.2, 0.8],
    [0, -1.1, 1.1],
    [1.8, 0.4, 0.75],
    [0.8, 1.6, 0.62],
  ]) {
    const dome = new THREE.Mesh(
      new THREE.SphereGeometry(size, 24, 12, 0, Math.PI * 2, 0, Math.PI / 2),
      material(0x78e3ed, { transparent: true, opacity: 0.56, metalness: 0.3 }),
    );
    dome.position.set(x, 0.14, z);
    dome.castShadow = true;
    parent.add(dome);
    cylinder(parent, size * 0.98, size * 0.98, 0.12, [x, 0.09, z], 0xc4e6e1, 24);
  }
  const ring = new THREE.Mesh(
    new THREE.TorusGeometry(3.7, 0.07, 10, 80),
    material(colors.cyan, { emissive: colors.cyan, emissiveIntensity: 1.2 }),
  );
  ring.rotation.x = Math.PI / 2;
  ring.position.y = 0.25;
  parent.add(ring);
  const shuttle = new THREE.Group();
  cylinder(shuttle, 0.14, 0.25, 0.9, [0, 0, 0], 0xe9f4f2, 12).rotation.z = Math.PI / 2;
  box(shuttle, [0.32, 0.04, 0.9], [0, 0, 0], 0x5ee3e7);
  parent.add(shuttle);
  animations.push((time) => {
    const angle = time * 0.42;
    shuttle.position.set(Math.cos(angle) * 4.8, 1.8 + Math.sin(time) * 0.25, Math.sin(angle) * 4.8);
    shuttle.rotation.y = -angle;
    ring.rotation.z = time * 0.07;
  });
}

function buildEra(root: THREE.Group, stage: number, animations: ((time: number) => void)[]) {
  const era = stage <= 1 ? 0 : stage <= 5 ? 1 : stage <= 8 ? 2 : 3;
  const prosperity = era === 0 ? stage : era === 1 ? stage - 2 : era === 2 ? stage - 6 : stage - 9;
  if (era < 3) {
    const water = cylinder(root, 7.1, 7.1, 0.18, [0, -0.25, 0], 0x66cbe3, 64);
    water.material = material(0x66cbe3, {
      roughness: 0.22,
      metalness: 0.05,
      transparent: true,
      opacity: 0.92,
    });
    cylinder(root, 6.05, 6.2, 0.32, [0, -0.08, 0], 0xe7cf94, 64);
    const island = cylinder(
      root,
      5.5,
      5.85,
      0.45,
      [0, 0, 0],
      era === 0 ? colors.darkGrass : colors.grass,
      48,
    );
    island.material = material(era === 0 ? colors.darkGrass : colors.grass, { roughness: 1 });
    const pathColor = era === 0 ? 0x9b7652 : 0xc8b990;
    box(root, [8.5, 0.035, 0.62], [0, 0.25, 0], pathColor, [0, 0.15, 0]);
    box(root, [0.65, 0.035, 8], [0, 0.26, 0], pathColor, [0, 0.15, 0]);
    for (let i = 0; i < 30 + prosperity * 6; i++) {
      const angle = i * 2.39;
      const radius = 2.5 + (i % 5) * 0.48;
      addFlower(
        root,
        Math.cos(angle) * radius,
        Math.sin(angle) * radius,
        [0xffd24d, 0xff7da8, 0x8b79f5, 0xffffff][i % 4],
      );
    }
    for (let i = 0; i < 14; i++) {
      const angle = i * 1.8;
      const stone = new THREE.Mesh(
        new THREE.DodecahedronGeometry(0.1 + (i % 3) * 0.035, 0),
        material(0xd9d4bd),
      );
      stone.position.set(Math.cos(angle) * 5.7, 0.28, Math.sin(angle) * 5.7);
      stone.scale.y = 0.55;
      root.add(stone);
    }
    addCloud(root, -3.5, 4.8, -3, animations, 0);
    addCloud(root, 4.2, 5.4, 1.5, animations, 2);
  }

  if (era === 0) {
    [
      [-2, -1.1, 0.4],
      [1.8, -1.4, -0.5],
      [-1.5, 1.6, 2.5],
      [1.7, 1.5, 3.6],
      [0, 2.8, 3.1],
    ].forEach(([x, z, r]) => addHut(root, x, z, r, animations));
    [
      [-3.1, -2.3, 0.6],
      [3.15, -2.2, -0.7],
      [-3.2, 2.15, 2.2],
      [3.1, 2.25, 3.7],
    ].forEach(([x, z, r]) => addHut(root, x, z, r, animations));
    addVillageWalls(root, true);
    addMarket(root, -0.9, 0.1, 0xefb44f);
    addMarket(root, 1.15, 0.05, 0xd45f58);
    for (let i = 0; i < 12; i++)
      addTree(root, Math.cos(i * 1.7) * (3.8 + (i % 2) * 0.6), Math.sin(i * 1.7) * 4, 0.8);
    addPerson(root, -0.5, 1.1, 0xbd594d, animations, 0);
    addPerson(root, 0.7, -0.9, 0xe0b34c, animations, 2);
    addPerson(root, -1.8, 0.2, 0x4d91b4, animations, 3);
    addPerson(root, 2.1, 0.8, 0x78a958, animations, 5);
    addPerson(root, -2.4, -1.5, 0xc66e92, animations, 7);
    for (let i = 0; i < prosperity; i++) {
      addHut(root, -3 + i * 2.8, 2.8 - i * 0.4, i, animations);
      addPerson(root, -2 + i * 1.3, -2 + i * 0.7, 0x65a6b8 + i * 0x080808, animations, i + 4);
    }
    if (prosperity >= 1) addBoat(root, animations);
    const fire = cylinder(root, 0.16, 0.28, 0.36, [0.1, 0.48, 1.2], 0xff9e43, 10);
    animations.push((time) =>
      fire.scale.set(1 + Math.sin(time * 8) * 0.12, 1 + Math.sin(time * 6) * 0.2, 1),
    );
    const field = new THREE.Group();
    for (let row = 0; row < 4; row++) {
      for (let col = 0; col < 6; col++) {
        cylinder(field, 0.025, 0.035, 0.27, [col * 0.16, 0.14, row * 0.17], 0xe0ba48, 5);
      }
    }
    field.position.set(2.4, 0.28, 0.8);
    root.add(field);
    for (let i = 0; i < 7; i++) {
      box(root, [0.07, 0.42, 0.07], [-3.15 + i * 0.35, 0.45, -0.4], 0x8b633b);
      box(root, [0.07, 0.42, 0.07], [-3.15 + i * 0.35, 0.45, 0.45], 0x8b633b);
    }
    box(root, [2.2, 0.06, 0.06], [-2.1, 0.55, -0.4], 0x9e7549);
    box(root, [2.2, 0.06, 0.06], [-2.1, 0.55, 0.45], 0x9e7549);
  } else if (era === 1) {
    const homes = [
      [-2.2, -1.4],
      [0.1, -2.25],
      [2.25, -1.2],
      [-2.1, 1.5],
      [1.8, 1.55],
      [0, 2.5],
    ];
    homes.forEach(([x, z], i) =>
      stage === 2
        ? addHanok(root, x, z, i === 2 ? 0.85 : 1)
        : addModernHouse(root, x, z, Math.min(2, stage - 3), stage === 5 ? 0.9 : 1),
    );
    if (stage === 2) addVillageWalls(root, false);
    addMarket(root, -0.8, 0.2, 0x5e93b4);
    addMarket(root, 0.85, 0.25, 0xd86752);
    for (let i = 0; i < 11; i++)
      addTree(root, Math.cos(i * 1.4) * 4.5, Math.sin(i * 1.4) * 4.3, 0.75);
    addPerson(root, -0.5, 1.1, 0x5c87b8, animations, 0);
    addPerson(root, 0.4, -0.8, 0xc95c58, animations, 2);
    addPerson(root, 1.5, 0.4, 0xe0b34c, animations, 4);
    if (stage >= 5) addTrain(root, animations);
    for (let i = 0; i < prosperity + 1; i++)
      addPerson(root, -2.4 + i * 1.25, 0.5, 0x4f8faa + i * 0x111100, animations, i + 6);
    if (prosperity >= 3) {
      addBoat(root, animations, 2.4);
      addBoat(root, animations, 4.4);
    }
  } else if (era === 2) {
    cylinder(root, 5.5, 5.85, 0.45, [0, 0, 0], 0x385b5d, 48);
    box(root, [10.2, 0.04, 1], [0, 0.26, 0], 0x243e47);
    box(root, [1, 0.04, 10.2], [0, 0.26, 0], 0x243e47);
    addCity(root, animations, prosperity);
    addPerson(root, -0.5, 1.2, 0x61c0c8, animations, 0);
    addPerson(root, 1, 1.1, 0xe5a84f, animations, 2);
    addPerson(root, -2, 0.4, 0xd66768, animations, 4);
    for (let i = 0; i < prosperity + 2; i++)
      addPerson(
        root,
        -2.8 + i * 1.1,
        2.1 - (i % 2) * 0.5,
        0x5bb6bc + i * 0x070500,
        animations,
        i + 7,
      );
  } else {
    if (stage === 9) addFloatingDistrict(root, animations);
    else addSpaceColony(root, animations);
    for (let i = 0; i < prosperity + 1; i++) {
      const moon = new THREE.Mesh(
        new THREE.SphereGeometry(0.35 + i * 0.08, 20, 20),
        material(0x6f86a7 + i * 0x111111),
      );
      root.add(moon);
      animations.push((time) => {
        const angle = time * (0.16 + i * 0.03) + i * 2;
        moon.position.set(Math.cos(angle) * (5.7 + i), 1.5 + i * 0.7, Math.sin(angle) * (5.7 + i));
      });
    }
  }
}

export default function ThreeVillage({ era, onTap }: Props) {
  const mountRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const mount = mountRef.current;
    if (!mount) return;

    const eraLevel = era <= 1 ? 0 : era <= 5 ? 1 : era <= 8 ? 2 : 3;
    const scene = new THREE.Scene();
    scene.fog = new THREE.FogExp2(
      eraLevel === 3 ? 0x020811 : 0xc6eef1,
      eraLevel === 3 ? 0.018 : 0.012,
    );
    const camera = new THREE.PerspectiveCamera(
      42,
      mount.clientWidth / mount.clientHeight,
      0.1,
      100,
    );
    camera.position.set(8.2, 7.1, 10.5);
    camera.lookAt(0, 0.6, 0);

    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.8));
    renderer.setSize(mount.clientWidth, mount.clientHeight);
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    renderer.outputColorSpace = THREE.SRGBColorSpace;
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = eraLevel === 3 ? 1.15 : 1.42;
    mount.appendChild(renderer.domElement);

    scene.add(new THREE.HemisphereLight(eraLevel === 3 ? 0x98bfff : 0xe9ffff, 0x5f8054, 3.35));
    const sun = new THREE.DirectionalLight(eraLevel === 3 ? 0x94bbff : 0xfff0b5, 4.4);
    sun.position.set(-6, 11, 7);
    sun.castShadow = true;
    sun.shadow.mapSize.set(1536, 1536);
    sun.shadow.camera.left = -8;
    sun.shadow.camera.right = 8;
    sun.shadow.camera.top = 8;
    sun.shadow.camera.bottom = -8;
    scene.add(sun);
    const rim = new THREE.PointLight(0x4de7e5, 16, 18);
    rim.position.set(5, 3, -5);
    scene.add(rim);

    if (eraLevel === 3) {
      const points = new Float32Array(900);
      for (let i = 0; i < points.length; i++) points[i] = (Math.random() - 0.5) * 35;
      const stars = new THREE.Points(
        new THREE.BufferGeometry().setAttribute("position", new THREE.BufferAttribute(points, 3)),
        new THREE.PointsMaterial({ color: 0xb9e8ff, size: 0.07, transparent: true, opacity: 0.8 }),
      );
      scene.add(stars);
    }

    const world = new THREE.Group();
    scene.add(world);
    const animations: ((time: number) => void)[] = [];
    buildEra(world, era, animations);

    let angle = 0.64;
    let elevation = 0.58;
    let distance = eraLevel === 3 ? 13.2 : 13;
    let dragging = false;
    let moved = false;
    let lastX = 0;
    let lastY = 0;
    const pointers = new Map<number, { x: number; y: number }>();
    let pinchDistance = 0;

    const updateCamera = () => {
      camera.position.set(
        Math.sin(angle) * Math.cos(elevation) * distance,
        Math.sin(elevation) * distance,
        Math.cos(angle) * Math.cos(elevation) * distance,
      );
      camera.lookAt(0, eraLevel === 3 ? 0.1 : 0.8, 0);
    };
    updateCamera();

    const down = (event: PointerEvent) => {
      renderer.domElement.setPointerCapture(event.pointerId);
      pointers.set(event.pointerId, { x: event.clientX, y: event.clientY });
      dragging = true;
      moved = false;
      lastX = event.clientX;
      lastY = event.clientY;
    };
    const move = (event: PointerEvent) => {
      pointers.set(event.pointerId, { x: event.clientX, y: event.clientY });
      if (pointers.size === 2) {
        const p = [...pointers.values()];
        const next = Math.hypot(p[0].x - p[1].x, p[0].y - p[1].y);
        if (pinchDistance)
          distance = THREE.MathUtils.clamp(distance * (pinchDistance / next), 8, 18);
        pinchDistance = next;
        moved = true;
        return;
      }
      if (!dragging) return;
      const dx = event.clientX - lastX;
      const dy = event.clientY - lastY;
      if (Math.abs(dx) + Math.abs(dy) > 2) moved = true;
      angle -= dx * 0.007;
      elevation = THREE.MathUtils.clamp(elevation + dy * 0.004, 0.25, 1.05);
      lastX = event.clientX;
      lastY = event.clientY;
      updateCamera();
    };
    const up = (event: PointerEvent) => {
      pointers.delete(event.pointerId);
      pinchDistance = 0;
      if (!moved && pointers.size === 0) onTap?.();
      dragging = false;
    };
    const wheel = (event: WheelEvent) => {
      event.preventDefault();
      distance = THREE.MathUtils.clamp(distance + event.deltaY * 0.008, 8, 18);
      updateCamera();
    };
    renderer.domElement.addEventListener("pointerdown", down);
    renderer.domElement.addEventListener("pointermove", move);
    renderer.domElement.addEventListener("pointerup", up);
    renderer.domElement.addEventListener("pointercancel", up);
    renderer.domElement.addEventListener("wheel", wheel, { passive: false });

    const clock = new THREE.Clock();
    let frame = 0;
    const render = () => {
      frame = requestAnimationFrame(render);
      const time = clock.getElapsedTime();
      animations.forEach((animate) => animate(time));
      renderer.render(scene, camera);
    };
    render();

    const resize = () => {
      if (!mount.clientWidth || !mount.clientHeight) return;
      camera.aspect = mount.clientWidth / mount.clientHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(mount.clientWidth, mount.clientHeight);
    };
    const resizeObserver = new ResizeObserver(resize);
    resizeObserver.observe(mount);

    return () => {
      cancelAnimationFrame(frame);
      resizeObserver.disconnect();
      renderer.domElement.removeEventListener("pointerdown", down);
      renderer.domElement.removeEventListener("pointermove", move);
      renderer.domElement.removeEventListener("pointerup", up);
      renderer.domElement.removeEventListener("pointercancel", up);
      renderer.domElement.removeEventListener("wheel", wheel);
      renderer.dispose();
      scene.traverse((object) => {
        if (object instanceof THREE.Mesh) {
          object.geometry.dispose();
          const materials = Array.isArray(object.material) ? object.material : [object.material];
          materials.forEach((entry) => entry.dispose());
        }
      });
      renderer.domElement.remove();
    };
  }, [era, onTap]);

  return (
    <div
      ref={mountRef}
      className="three-village"
      aria-label="회전하고 확대할 수 있는 3D 성장 마을"
    />
  );
}
