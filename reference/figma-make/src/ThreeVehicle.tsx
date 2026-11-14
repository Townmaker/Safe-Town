import { useEffect, useRef } from "react";
import * as THREE from "three";

export type VehiclePart = "battery" | "tire" | "brake" | "coolant";

export default function ThreeVehicle({ selected }: { selected: VehiclePart }) {
  const mountRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const mount = mountRef.current;
    if (!mount) return;
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(38, mount.clientWidth / mount.clientHeight, 0.1, 50);
    camera.position.set(5.8, 3.3, 7.4);
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.6));
    renderer.setSize(mount.clientWidth, mount.clientHeight);
    renderer.shadowMap.enabled = true;
    renderer.outputColorSpace = THREE.SRGBColorSpace;
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.3;
    mount.appendChild(renderer.domElement);

    scene.add(new THREE.HemisphereLight(0xdffcff, 0x20303a, 3));
    const light = new THREE.DirectionalLight(0xffffff, 4);
    light.position.set(4, 8, 6);
    light.castShadow = true;
    scene.add(light);
    const rim = new THREE.PointLight(0x55e1eb, 18, 12);
    rim.position.set(-4, 2, -3);
    scene.add(rim);

    const car = new THREE.Group();
    scene.add(car);
    const paint = new THREE.MeshStandardMaterial({
      color: 0x5babb9,
      metalness: 0.72,
      roughness: 0.24,
    });
    const glass = new THREE.MeshStandardMaterial({
      color: 0x173544,
      metalness: 0.4,
      roughness: 0.18,
      transparent: true,
      opacity: 0.88,
    });
    const body = new THREE.Mesh(new THREE.BoxGeometry(4.1, 0.75, 1.9), paint);
    body.position.y = 0.82;
    body.geometry.translate(0, 0, 0);
    body.castShadow = true;
    car.add(body);
    const hood = new THREE.Mesh(new THREE.BoxGeometry(1.25, 0.3, 1.78), paint);
    hood.position.set(1.55, 1.25, 0);
    hood.rotation.z = -0.08;
    car.add(hood);
    const cabin = new THREE.Mesh(new THREE.BoxGeometry(1.85, 0.85, 1.62), glass);
    cabin.position.set(-0.35, 1.55, 0);
    cabin.rotation.z = -0.03;
    car.add(cabin);
    const windshield = new THREE.Mesh(new THREE.BoxGeometry(0.68, 0.68, 1.66), glass);
    windshield.position.set(0.56, 1.53, 0);
    windshield.rotation.z = -0.58;
    car.add(windshield);
    const rearGlass = new THREE.Mesh(new THREE.BoxGeometry(0.48, 0.62, 1.64), glass);
    rearGlass.position.set(-1.25, 1.48, 0);
    rearGlass.rotation.z = 0.5;
    car.add(rearGlass);
    const trim = new THREE.MeshStandardMaterial({
      color: 0xd6ecec,
      metalness: 0.82,
      roughness: 0.16,
    });
    const darkTrim = new THREE.MeshStandardMaterial({
      color: 0x20343c,
      metalness: 0.55,
      roughness: 0.28,
    });
    for (const z of [-0.98, 0.98]) {
      const sill = new THREE.Mesh(new THREE.BoxGeometry(2.8, 0.09, 0.08), darkTrim);
      sill.position.set(-0.2, 0.54, z);
      car.add(sill);
      const mirror = new THREE.Mesh(new THREE.SphereGeometry(0.18, 16, 10), paint);
      mirror.scale.set(1.3, 0.55, 0.8);
      mirror.position.set(0.45, 1.55, z * 1.08);
      car.add(mirror);
      for (const x of [-0.65, 0.25]) {
        const handle = new THREE.Mesh(new THREE.BoxGeometry(0.28, 0.045, 0.035), trim);
        handle.position.set(x, 1.12, z * 1.005);
        car.add(handle);
      }
    }
    const grille = new THREE.Mesh(new THREE.BoxGeometry(0.06, 0.38, 1.25), darkTrim);
    grille.position.set(2.08, 0.82, 0);
    car.add(grille);
    const bumperFront = new THREE.Mesh(new THREE.BoxGeometry(0.18, 0.2, 1.94), trim);
    bumperFront.position.set(2.1, 0.48, 0);
    car.add(bumperFront);
    const bumperRear = bumperFront.clone();
    bumperRear.position.x = -2.1;
    car.add(bumperRear);
    const lightMaterial = new THREE.MeshStandardMaterial({
      color: 0xe9ffff,
      emissive: 0xb7fbff,
      emissiveIntensity: 1.4,
    });
    const tailMaterial = new THREE.MeshStandardMaterial({
      color: 0xff5965,
      emissive: 0xff303f,
      emissiveIntensity: 1.1,
    });
    for (const z of [-0.63, 0.63]) {
      const headlight = new THREE.Mesh(new THREE.BoxGeometry(0.08, 0.22, 0.42), lightMaterial);
      headlight.position.set(2.08, 1.02, z);
      car.add(headlight);
      const tail = new THREE.Mesh(new THREE.BoxGeometry(0.08, 0.2, 0.38), tailMaterial);
      tail.position.set(-2.08, 1.02, z);
      car.add(tail);
    }
    const panoramicRoof = new THREE.Mesh(new THREE.BoxGeometry(1.18, 0.04, 1.18), glass);
    panoramicRoof.position.set(-0.35, 2, 0);
    car.add(panoramicRoof);
    const wheelMaterial = new THREE.MeshStandardMaterial({ color: 0x11181d, roughness: 0.9 });
    const brakeMaterial = new THREE.MeshStandardMaterial({
      color: 0x6b7277,
      metalness: 0.7,
      roughness: 0.3,
    });
    const highlights: Record<VehiclePart, THREE.Object3D[]> = {
      battery: [],
      tire: [],
      brake: [],
      coolant: [],
    };
    for (const x of [-1.32, 1.35]) {
      for (const z of [-1, 1]) {
        const wheel = new THREE.Mesh(
          new THREE.CylinderGeometry(0.48, 0.48, 0.34, 24),
          wheelMaterial,
        );
        wheel.rotation.x = Math.PI / 2;
        wheel.position.set(x, 0.58, z * 0.92);
        wheel.castShadow = true;
        car.add(wheel);
        highlights.tire.push(wheel);
        const rimMesh = new THREE.Mesh(new THREE.CylinderGeometry(0.27, 0.27, 0.36, 20), trim);
        rimMesh.rotation.x = Math.PI / 2;
        rimMesh.position.copy(wheel.position);
        car.add(rimMesh);
        for (let spoke = 0; spoke < 5; spoke++) {
          const spokeMesh = new THREE.Mesh(new THREE.BoxGeometry(0.28, 0.025, 0.04), darkTrim);
          spokeMesh.position.copy(wheel.position);
          spokeMesh.position.z += z * 0.2;
          spokeMesh.rotation.z = (spoke / 5) * Math.PI * 2;
          car.add(spokeMesh);
        }
        const brake = new THREE.Mesh(
          new THREE.CylinderGeometry(0.25, 0.25, 0.37, 20),
          brakeMaterial,
        );
        brake.rotation.x = Math.PI / 2;
        brake.position.copy(wheel.position);
        car.add(brake);
        highlights.brake.push(brake);
      }
    }
    const battery = new THREE.Mesh(
      new THREE.BoxGeometry(0.85, 0.25, 0.95),
      new THREE.MeshStandardMaterial({ color: 0x557177, metalness: 0.4 }),
    );
    battery.position.set(0.95, 1.28, 0);
    car.add(battery);
    highlights.battery.push(battery);
    const coolant = new THREE.Mesh(
      new THREE.CylinderGeometry(0.18, 0.23, 0.42, 16),
      new THREE.MeshStandardMaterial({ color: 0xa4e7e7, transparent: true, opacity: 0.8 }),
    );
    coolant.position.set(1.62, 1.45, -0.48);
    car.add(coolant);
    highlights.coolant.push(coolant);

    const floor = new THREE.Mesh(
      new THREE.CircleGeometry(4.2, 48),
      new THREE.MeshStandardMaterial({
        color: 0x17323c,
        roughness: 0.85,
        transparent: true,
        opacity: 0.65,
      }),
    );
    floor.rotation.x = -Math.PI / 2;
    floor.position.y = 0.08;
    floor.receiveShadow = true;
    scene.add(floor);

    const targets: Record<VehiclePart, THREE.Vector3> = {
      battery: new THREE.Vector3(3.9, 2.35, 4.4),
      coolant: new THREE.Vector3(4.15, 2.55, -2.8),
      tire: new THREE.Vector3(3.55, 1.6, 3.4),
      brake: new THREE.Vector3(3.2, 1.35, 2.8),
    };
    let frame = 0;
    const clock = new THREE.Clock();
    const render = () => {
      frame = requestAnimationFrame(render);
      const time = clock.getElapsedTime();
      camera.position.lerp(targets[selected], 0.045);
      camera.lookAt(selected === "coolant" ? 1.3 : selected === "battery" ? 0.75 : 0, 0.9, 0);
      car.rotation.y = Math.sin(time * 0.25) * 0.12;
      highlights[selected].forEach((mesh) => {
        const entry = mesh as THREE.Mesh;
        if (entry.material instanceof THREE.MeshStandardMaterial) {
          entry.material.emissive.setHex(0xff3f4f);
          entry.material.emissiveIntensity = 0.8 + Math.sin(time * 6) * 0.65;
        }
      });
      renderer.render(scene, camera);
    };
    render();
    const observer = new ResizeObserver(() => {
      camera.aspect = mount.clientWidth / mount.clientHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(mount.clientWidth, mount.clientHeight);
    });
    observer.observe(mount);
    return () => {
      cancelAnimationFrame(frame);
      observer.disconnect();
      renderer.dispose();
      scene.traverse((object) => {
        if (object instanceof THREE.Mesh) {
          object.geometry.dispose();
          const entries = Array.isArray(object.material) ? object.material : [object.material];
          entries.forEach((entry) => entry.dispose());
        }
      });
      renderer.domElement.remove();
    };
  }, [selected]);

  return (
    <div
      className="three-vehicle"
      ref={mountRef}
      aria-label="선택한 차량 부품을 강조하는 3D 차량"
    />
  );
}
